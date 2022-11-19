package com.javatechie.executor.api.controller;

import com.javatechie.executor.api.entity.User;
import com.javatechie.executor.api.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            service.saveUsers(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/users", produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllUsers() {
       return  service.findAllUsers().thenApply(ResponseEntity::ok);
    }


    @GetMapping(value = "/getUsersByThread", produces = "application/json")
    public  ResponseEntity getUsers(){
        CompletableFuture<List<User>> users1=service.findAllUsers();
        CompletableFuture<List<User>> users2=service.findAllUsers();
        CompletableFuture<List<User>> users3=service.findAllUsers();
        CompletableFuture.allOf(users1,users2,users3).join();
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

     // https://stackoverflow.com/questions/69533142/spring-boot-async-await-that-all-thread-completed
     @GetMapping(value = "/getUsersByThreadAll", produces = "application/json")
     public ResponseEntity<String> getUsersAll() {
 
         long before = System.currentTimeMillis();
 
         Collection<Future<Void>> futures = new ArrayList<Future<Void>>();
 
         for (int i = 0; i < 100; i++) {
             UUID uuid = UUID.randomUUID();
             String uid = uuid.toString();
             futures.add(service.findAllUsersToT(uid));
         }
 
         for (Future<Void> future : futures) {
             try {
                 future.get();
             } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
 
         long after = System.currentTimeMillis();
 
         logger.info("done");
 
         // service.mymap.put(uid, uid);
 
         return new ResponseEntity<>("done", HttpStatus.OK);
 
     }
}
