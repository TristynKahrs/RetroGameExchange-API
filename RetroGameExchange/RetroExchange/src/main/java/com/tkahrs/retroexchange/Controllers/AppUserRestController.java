package com.tkahrs.retroexchange.Controllers;

import com.tkahrs.retroexchange.Models.AppUser;
import com.tkahrs.retroexchange.Repositories.AppUserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/user") // http://localhost:8080/user/createUser
public class AppUserRestController {
//    {
//        "name":"Tristyn Kahrs",
//            "password":"mypass",
//            "email":"tkahrs@student.neumont.edu",
//            "address":"343 S 500 E"
//    }
    @Autowired
    private AppUserJpaRepository userJpa;

    @Autowired
    private UserDetailsManager udm;

    @Autowired
    private PasswordEncoder pswdEnc;

    static ArrayList<Link> generateUserLinks(int id){
        ArrayList<Link> links = new ArrayList<>();
        Link selfLink = linkTo(AppUserRestController.class).slash(id).withSelfRel();
        Link linkPOJO = Link.of(String.format("http://localhost:8080/user/%s", id), "appUsers"); //adds the userPOJO link
        Link linkGameList = Link.of(String.format("http://localhost:8080/games/user/%s", id), "gameList"); //adds the gameList link
        links.add(selfLink);
        links.add(linkPOJO);
        links.add(linkGameList);
        return links;
    }

//    @GetMapping(path = "/all")
//    public List<AppUser> getAllUsers() {
//        List<AppUser> allUsers = userJpa.findAll();
//        for(AppUser user : allUsers) {
//            for(Link link : generateUserLinks(user.getId())){
//                user.add(link);
//            }
//        }
//        return allUsers;
//    }

    @GetMapping(path = "/all")
    public CollectionModel<AppUser> getAllUsers() {
        List<AppUser> allUsers = userJpa.findAll();
        for(AppUser user : allUsers) {
            Link selfLink = linkTo(AppUserRestController.class).slash(user.getId()).withSelfRel();
            user.add(selfLink);
            // TODO add game links and order links
        }
        Link link = linkTo(AppUserRestController.class).withSelfRel();
        return CollectionModel.of(allUsers, link);
    }

    @GetMapping(path = {"/", "/{id}"})
    public AppUser getUser(@RequestHeader(value="Authorization") String authorizationHeader, @PathVariable(required = false) Integer id) {
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        if(id != null) {
            curUser = userJpa.getById(id);
        }
        for(Link link : generateUserLinks(curUser.getId())){
            curUser.add(link);
        }
        return curUser;
    }

    @PostMapping(path = "/createUser")
    @ResponseStatus(code= HttpStatus.CREATED)
    public AppUser createUser(@RequestBody AppUser testUser) {
        if (userJpa.getFirstByEmail(testUser.getEmail()).isPresent()){
            throw new KeyAlreadyExistsException("A user with that email already exists!");
        }
        userJpa.save(testUser);
        UserDetails newUser = User.withUsername(testUser.getEmail())
                .password(pswdEnc.encode(testUser.getPassword()))
                .roles("USER").build();
        udm.createUser(newUser);
        for(Link link : generateUserLinks(testUser.getId())){
            testUser.add(link); //puts all the generated links into the user
        }
        return testUser; // isn't doing links for some reason
    }

    @GetMapping(path="/forgotPassword")
    public void sendTemporaryPassword(@RequestParam String username, @RequestParam String email) {
        //Remove Old User From InMemoryUserDetailsManager
        String tempNums = "1234567890";
        String tempLowLetters = "qwertyuiopasdfghjklmnbvcxz";
        String tempUpLetters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String tempChars = "!@#$%^&*()[]{};:'<>,.\\/?";

        StringBuilder tempPassword = new StringBuilder();
        Random rand = new Random();
        while(tempPassword.length() < 16) {
            tempPassword.append(tempNums.charAt(rand.nextInt(tempNums.length())));
            tempPassword.append(tempLowLetters.charAt(rand.nextInt(tempLowLetters.length())));
            tempPassword.append(tempUpLetters.charAt(rand.nextInt(tempUpLetters.length())));
            tempPassword.append(tempChars.charAt(rand.nextInt(tempChars.length())));
        }

        AppUser currentUser = userJpa.findByNameAndEmail(username, email);
        currentUser.setPassword(tempPassword.toString());
        userJpa.save(currentUser);

        UserDetails newUser = User.withUsername(currentUser.getEmail())
                .password(pswdEnc.encode(currentUser.getPassword()))
                .roles("USER").build();
        udm.updateUser(newUser);

        try {
            String params = String.format("address:%s,subject:Recovery Password,body:Your temporary password is '%s'", email, currentUser.getPassword());
            new EmailQueue(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        new SendMail(email, "Recovery Password", "Here is your temporary password: " + currentUser.getPassword());
    }

    @PutMapping("/")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public AppUser updateUser(@RequestHeader(value="Authorization") String authorizationHeader, @RequestBody AppUser updateUser){
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        curUser.setName(updateUser.getName());
        curUser.setPassword(updateUser.getPassword());
        curUser.setAddress(updateUser.getAddress());
        this.userJpa.save(curUser);
        for(Link link : generateUserLinks(curUser.getId())){
            curUser.add(link);
        }
        UserDetails newUser = User.withUsername(curUser.getEmail())
                .password(pswdEnc.encode(curUser.getPassword()))
                .roles("USER").build();
        udm.updateUser(newUser);
        return curUser;
    }

    @DeleteMapping(path = "/delete")
    public void deleteUser(@RequestHeader(value="Authorization") String authorizationHeader) {
        String email = BLL.decodeAuth(authorizationHeader)[0];
        AppUser curUser = userJpa.getByEmail(email);
        udm.deleteUser(email);
        this.userJpa.delete(curUser);
    }
}
