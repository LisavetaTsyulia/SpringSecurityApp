package net.proselyte.springsecurityapp.controller;

import com.google.gson.Gson;
import net.proselyte.springsecurityapp.model.Product;
import net.proselyte.springsecurityapp.model.User;
import net.proselyte.springsecurityapp.service.SearchService;
import net.proselyte.springsecurityapp.service.SecurityService;
import net.proselyte.springsecurityapp.service.UserService;
import net.proselyte.springsecurityapp.validator.UserValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ibm.icu.text.Transliterator;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getConfirmPassword());

        model.addAttribute("theUser", userForm);

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Username or password is incorrect.");
        }

        if (logout != null) {
            model.addAttribute("message", "Logged out successfully.");
        }
        return "login";
    }


    @RequestMapping(value = {"/welcome2/{userId}"}, method = RequestMethod.GET)
    public String findOwner (@PathVariable String userId,@RequestParam("button") String button, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        User currentUser = userService.findByUsername(name);
        if (currentUser.getStatus().equals("BLOCKED")) {
            return "/new";
        }
        Long id = Long.parseLong(userId);
        model.addAttribute("userStatus", userService.findById(id));
        if(button.equals("delete"))
            userService.delete(id);
        else
            userService.block(id);
        model.addAttribute("userList", userService.getAllUsers());
        return  "welcome" ;
    }

    @RequestMapping(value = {"/" , "/welcome*"}, method = RequestMethod.GET)
    public String welcome(@RequestParam(value = "first_name", required=false) String firstName,
                          @RequestParam(value = "last_name", required=false) String lastName,
                          @RequestParam(value = "uid", required=false) String id, Model model) {
        if(firstName == null && id == null){
            model.addAttribute("userList", userService.getAllUsers());
        } else {
            User user = new User();
            user.setStatus("ACTIVE");
            user.setPassword(id);
            String greek
                    = firstName + lastName;
            String id2 = "Any-Latin; NFD; [^\\p{Alnum}] Remove";
            String latin = Transliterator.getInstance(id2)
                    .transform(greek);
            user.setUsername(latin);
            System.out.println(latin);

            userService.save(user);

            securityService.autoLogin(user.getUsername(), user.getPassword());

            model.addAttribute("userList", userService.getAllUsers());
        }
            return "welcome";

    }

    @RequestMapping(value = {"/getmore"}, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getMore() {
        List<Product> productList = searchService.getNextPage();
        Gson gson = new Gson();
        return gson.toJson(productList);
    }

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String search(@RequestParam String request, Model model) {
        try {
            String req2 = URLEncoder.encode(request, "utf-8");
            model.addAttribute("request", request);
            List<Product> products = searchService.getFirstPage(req2.replaceAll(" ", "+"));
            model.addAttribute("products", products);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "search";
    }

    @RequestMapping(value = "/user-list/change")
    public String userList(@RequestParam("personId") Long[] userIds, @RequestParam("button") String button, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        User currentUser = userService.findByUsername(name);
        if (currentUser.getStatus().equals("BLOCKED")) {
            return "new";
        }

        if (button.equals("deleteButton")) {
            for (long userId : userIds)
                userService.delete(userId);
        } else {
            for (long userId : userIds)
                userService.block(userId);
        }
        model.addAttribute("userList", userService.getAllUsers());
        return "welcome";
    }
}
