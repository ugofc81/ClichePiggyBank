package com.example.clichepiggybank.controller;

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
        Collections.sort(sanctionList, Comparator.comparing(Sanction::getDatetime));
        int limit = Math.min(sanctionList.size(), AMOUNT_OF_LAST_SANCTIONS);
        return ResponseEntity.ok(sanctionList.subList(sanctionList.size()-limit, sanctionList.size()));
    }

    @PostMapping
    public ResponseEntity<Sanction> createSanction(@RequestBody Sanction newSanction) {
        HashMap<String, Sanction> current = sanctionStorageService.loadSanctions();
        String guid = UUID.randomUUID().toString();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID().toString();
        }
        newSanction.setId(guid);
        newSanction.setDatetime(new Date());

        User reporter = newSanction.getReporter();
        User receiver = newSanction.getReceiver();
        UserController userController = new UserController(userStorageService);
        AccountController accountController = new AccountController(accountStorageService);
        User savedReporter = userController.getUser(reporter.getId()).getBody();
        newSanction.getReporter().setName(savedReporter.getName());
        User savedReceiver = userController.getUser(receiver.getId()).getBody();
        newSanction.getReceiver().setName(savedReceiver.getName());

        HashMap<String, Account> accounts = accountController.getAllAccounts().getBody();
        Account toBeCharged = accounts.values().stream().filter(account -> account.getOwnerId().equals(receiver.getId())).findFirst().orElse(null);
        if (toBeCharged == null) {
            toBeCharged = accountController.createAccount(receiver).getBody();
        }
        toBeCharged.setBalance(toBeCharged.getBalance() + newSanction.getAmount());
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
}
