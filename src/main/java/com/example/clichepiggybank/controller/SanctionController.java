package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.controller.exceptions.ForbiddenException;
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
@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<List<Sanction>> getAllSanctions(@RequestParam("inquirerid") UUID inquirerId) {
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        return ResponseEntity.ok(new ArrayList<>(sanctionStorageService.loadSanctions().values()));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Sanction>> getLastSanctions(@RequestParam("inquirerid") UUID inquirerId) {
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        List<Sanction> sanctionList = getAllSanctions(inquirerId).getBody();
        Collections.sort(sanctionList, Comparator.comparing(Sanction::getDatetime).reversed());
        int limit = Math.min(sanctionList.size(), AMOUNT_OF_LAST_SANCTIONS);
        return ResponseEntity.ok(sanctionList.subList(0, limit));
    }

    @GetMapping("/best")
    public ResponseEntity<List<Sanction>> getBestSanctions(@RequestParam("inquirerid") UUID inquirerId) {
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        List<Sanction> sanctionList = getAllSanctions(inquirerId).getBody();
        Collections.sort(sanctionList, Comparator.comparing(Sanction::getLikes).reversed());
        return ResponseEntity.ok(sanctionList);
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<Sanction>> paginateSanctions(
            @RequestParam(name = "sort", defaultValue = "datetime") String sort,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam("inquirerid") UUID inquirerId) {
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        List<Sanction> sanctionList = getAllSanctions(inquirerId).getBody();
        Comparator c;
        switch(sort) {
            case "likes":
                c = Comparator.comparing(Sanction::getLikes);
                break;
            default:
                c = Comparator.comparing(Sanction::getDatetime);
        }
        switch(order) {
            case "desc":
                c = c.reversed();
                break;
        }
        Collections.sort(sanctionList, c);
        Integer offset = Math.min(Math.max((page-1) * size, 0), sanctionList.size());
        return ResponseEntity.ok(sanctionList.subList(offset, offset+Math.min(sanctionList.size()-offset, size)));
    }

    @PostMapping
    public ResponseEntity<Sanction> createSanction(@RequestBody Sanction newSanction, @RequestParam("inquirerid") UUID inquirerId) {
        HashMap<UUID, Sanction> current = sanctionStorageService.loadSanctions();
        UUID guid = UUID.randomUUID();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID();
        }
        newSanction.setId(guid);
        newSanction.setDatetime(new Date());

        User receiver = newSanction.getReceiver();
        UserController userController = new UserController(userStorageService, accountStorageService);
        AccountController accountController = new AccountController(accountStorageService, userStorageService);
        User reporter = userController.getUser(inquirerId).getBody();
        if (!Arrays.asList(reporter.getRoles()).contains("user") && !Arrays.asList(reporter.getRoles()).contains("admin")) {
            throw new ForbiddenException(inquirerId);
        }
        newSanction.setReporter(reporter);
        User savedReceiver = userController.getUser(receiver.getId()).getBody();
        newSanction.getReceiver().setName(savedReceiver.getName());
        newSanction.setLikedBy(Collections.emptySet());
        newSanction.setLikes(0);

        List<Account> accounts = accountController.getAllAccounts().getBody();
        Account toBeCharged = accounts.stream().filter(account -> account.getOwnerId().equals(receiver.getId())).findFirst().orElse(null);
        if (toBeCharged == null) {
            toBeCharged = accountController.createAccount(receiver).getBody();
        }
        toBeCharged.setBalance(toBeCharged.getBalance() + newSanction.getAmount().amount());
        accountController.updateAccount(toBeCharged.getId(), toBeCharged);
        current.put(guid, newSanction);
        sanctionStorageService.saveSanctions(current);
        return ResponseEntity.ok(newSanction);
    }

    public ResponseEntity<Sanction> getSanction(@PathVariable UUID id) {
        HashMap<UUID, Sanction> sanctions = sanctionStorageService.loadSanctions();
        if(!sanctions.containsKey(id)) {
            throw new SanctionNotFoundException(id);
        }
        return ResponseEntity.ok(sanctions.get(id));
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<Sanction> likeSanction(@PathVariable UUID id, @RequestParam("inquirerid") UUID inquirerId) {
        HashMap<UUID, Sanction> sanctions = sanctionStorageService.loadSanctions();
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!sanctions.containsKey(id)) {
            throw new SanctionNotFoundException(id);
        }
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        Sanction sanction = sanctions.get(id);
        sanction.getLikedBy().add(inquirerId);
        sanction.setLikes(sanction.getLikedBy().size());
        sanctions.put(id, sanction);
        sanctionStorageService.saveSanctions(sanctions);
        return ResponseEntity.ok(sanction);
    }

    @PutMapping("/{id}/unlike")
    public ResponseEntity<Sanction> unlikeSanction(@PathVariable UUID id, @RequestParam("inquirerid") UUID inquirerId) {
        HashMap<UUID, Sanction> sanctions = sanctionStorageService.loadSanctions();
        HashMap <UUID, User> users = userStorageService.loadUsers();
        if(!sanctions.containsKey(id)) {
            throw new SanctionNotFoundException(id);
        }
        if(!users.containsKey(inquirerId)) {
            throw new InquirerNotFoundException(inquirerId);
        }
        if (!UserController.isUser(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        Sanction sanction = sanctions.get(id);
        sanction.getLikedBy().remove(inquirerId);
        sanction.setLikes(sanction.getLikedBy().size());
        sanctions.put(id, sanction);
        sanctionStorageService.saveSanctions(sanctions);
        return ResponseEntity.ok(sanction);
    }
}
