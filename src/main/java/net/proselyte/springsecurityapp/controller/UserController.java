package net.proselyte.springsecurityapp.controller;

import net.proselyte.springsecurityapp.model.User;
import net.proselyte.springsecurityapp.service.SecurityService;
import net.proselyte.springsecurityapp.service.UserService;
import net.proselyte.springsecurityapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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
        Long id = Long.parseLong(userId);
        model.addAttribute("userStatus", userService.findById(id));
        if(button.equals("delete"))
            userService.delete(id);
        else
            userService.block(id);
        model.addAttribute("userList", userService.getAllUsers());
        return  "welcome" ;
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(@ModelAttribute("userName") String userName, Model model) {
        model.addAttribute("userList", userService.getAllUsers());
        //model.addAttribute("userStatus", userService.findByUsername(userName).getStatus());
        return "welcome";
    }

    @RequestMapping(value = {"/redirect"}, method = RequestMethod.GET)
    public String redirect(Model model) {
        return "new";
    }

}
