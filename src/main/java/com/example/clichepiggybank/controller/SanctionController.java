package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.controller.exceptions.InquirerNotFoundException;
import com.example.clichepiggybank.controller.exceptions.SanctionNotFoundException;
import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.Sanction;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.SanctionStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/sanctions")
public class SanctionController {
    private final SanctionStorageService sanctionStorageService;
    private final UserStorageService userStorageService;
    private final AccountStorageService accountStorageService;
    private final static int AMOUNT_OF_LAST_SANCTIONS = 10;

    public SanctionController(SanctionStorageService sanctionStorageService, UserStorageService userStorageService, AccountStorageService accountStorageService) {
        this.sanctionStorageService = sanctionStorageService;
        this.userStorageService = userStorageService;
        this.accountStorageService = accountStorageService;
    }

    @GetMapping
    public ResponseEntity<List<Sanction>> getAllSanctions() {
        return ResponseEntity.ok(new ArrayList<>(sanctionStorageService.loadSanctions().values()));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Sanction>> getLastSanctions() {
        List<Sanction> sanctionList = getAllSanctions().getBody();
        Collections.sort(sanctionList, Comparator.comparing(Sanction::getDatetime).reversed());
        int limit = Math.min(sanctionList.size(), AMOUNT_OF_LAST_SANCTIONS);
        return ResponseEntity.ok(sanctionList.subList(0, limit));
    }

    @GetMapping("/best")
    public ResponseEntity<List<Sanction>> getBestSanctions() {
        List<Sanction> sanctionList = getAllSanctions().getBody();
        Collections.sort(sanctionList, Comparator.comparing(Sanction::getLikes).reversed());
        return ResponseEntity.ok(sanctionList);
    }

    @PostMapping
    public ResponseEntity<Sanction> createSanction(@RequestBody Sanction newSanction, @RequestParam("inquirerid") String inquirerId) {
        HashMap<String, Sanction> current = sanctionStorageService.loadSanctions();
        String guid = UUID.randomUUID().toString();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID().toString();
        }
        newSanction.setId(guid);
        newSanction.setDatetime(new Date());

        User receiver = newSanction.getReceiver();
        UserController userController = new UserController(userStorageService, accountStorageService);
        AccountController accountController = new AccountController(accountStorageService, userStorageService);
        User reporter = userController.getUser(inquirerId).getBody();
        newSanction.setReporter(reporter);
        User savedReceiver = userController.getUser(receiver.getId()).getBody();
        newSanction.getReceiver().setName(savedReceiver.getName());
        newSanction.setLikedBy(Collections.emptySet());
        newSanction.setLikes(0);

        HashMap<String, Account> accounts = accountController.getAllAccounts().getBody();
        Account toBeCharged = accounts.values().stream().filter(account -> account.getOwnerId().equals(receiver.getId())).findFirst().orElse(null);
        if (toBeCharged == null) {
            toBeCharged = accountController.createAccount(receiver).getBody();
        }
        toBeCharged.setBalance(toBeCharged.getBalance() + newSanction.getAmount().amount());
        accountController.updateAccount(toBeCharged.getId(), toBeCharged);
        current.put(guid, newSanction);
        sanctionStorageService.saveSanctions(current);
        return ResponseEntity.ok(newSanction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sanction> getSanction(@PathVariable String id) {
        HashMap<String, Sanction> sanctions = sanctionStorageService.loadSanctions();
        if(!sanctions.containsKey(id)) {
            throw new SanctionNotFoundException(id);
        }
        return ResponseEntity.ok(sanctions.get(id));
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<Sanction> likeSanction(@PathVariable String id, @RequestParam("inquirerid") String inquirerId) {
        HashMap<String, Sanction> sanctions = sanctionStorageService.loadSanctions();
        UserController userController = new UserController(userStorageService, accountStorageService);
        HashMap <String, User> users = userController.getAllUsers().getBody();
        if(!sanctions.containsKey(id)) {
            throw new SanctionNotFoundException(id);
        }
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(id);
        }
        Sanction sanction = sanctions.get(id);
        sanction.getLikedBy().add(inquirerId);
        sanction.setLikes(sanction.getLikedBy().size());
        sanctions.put(id, sanction);
        sanctionStorageService.saveSanctions(sanctions);
        return ResponseEntity.ok(sanction);
    }
}
