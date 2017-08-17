package net.proselyte.springsecurityapp.controller;

import net.proselyte.springsecurityapp.model.User;
import net.proselyte.springsecurityapp.service.SecurityService;
import net.proselyte.springsecurityapp.service.UserService;
import net.proselyte.springsecurityapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ibm.icu.text.Transliterator;



@Controller
public class UserController {

    @Autowired
    private UserService userService;

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


    @RequestMapping(value = {"/welcome/{userId}"}, method = RequestMethod.GET)
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

    @RequestMapping(value = {"/" , "/welcome"})
    public String welcome(Model model) {
        model.addAttribute("userList", userService.getAllUsers());
        return "welcome";
    }

    @RequestMapping(value = {"/redirect"}, method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String Submit(@RequestParam("first_name") String firstName,@RequestParam("last_name") String lastName,
                  @RequestParam("uid") String id, Model model) {

        User user = new User();
        user.setStatus("ACTIVE");
        user.setPassword(id);
        String greek
                = firstName+lastName;
        String id2 = "Any-Latin; NFD; [^\\p{Alnum}] Remove";
        String latin = Transliterator.getInstance(id2)
                .transform(greek);
        user.setUsername(latin);
        System.out.println(latin);

        userService.save(user);

        securityService.autoLogin(user.getUsername(), user.getPassword());

        model.addAttribute("theUser", user);

        return "redirect:/welcome";
    }




}
