package com.aristideniyungeko;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TODO: documentation.
 */
@Controller
public class ContactsController {

   @Autowired private ContactsDAO contactsDAO;

   @Autowired private ContactFormValidator validator;

   Logger logger = Logger.getRootLogger();

   public void initBinder(WebDataBinder binder) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setLenient(false);
      binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
   }

   @RequestMapping("/searchContacts")
   public ModelAndView searchContacts(@RequestParam(required = false, defaultValue = "") String name) {
      // TODO make the param searchContacts?
      ModelAndView mav = new ModelAndView("showContacts");
      List<Contact> contacts = contactsDAO.searchContacts(name.trim());
      mav.addObject("SEARCH_CONTACTS_RESULTS_KEY", contacts);
      return mav;
   }

   @RequestMapping("/viewAllContacts")
   public ModelAndView getAllContacts() {
      ModelAndView mav = new ModelAndView("showContacts");
      List<Contact> contacts = contactsDAO.getAllContacts();
      mav.addObject("SEARCH_CONTACTS_RESULTS_KEY", contacts);
      return mav;
   }

   @RequestMapping(value="/saveContact", method = RequestMethod.GET)
   public ModelAndView newUserForm() {
      ModelAndView mav = new ModelAndView("newContact");
      Contact contact = new Contact();
      mav.getModelMap().put("newContact", contact);
      return mav;
   }

   @RequestMapping(value="/saveContact", method = RequestMethod.POST)
   public String create(@ModelAttribute("newContact") Contact contact,
                        BindingResult result,
                        SessionStatus status) {
      validator.validate(contact, result);
      if (result.hasErrors()) {
         return "newContact";
      }
      contactsDAO.save(contact);
      status.setComplete();
      return "redirect:viewAllContacts.do";
   }

   @RequestMapping(value = "/updateContact", method = RequestMethod.GET)
   public ModelAndView edit(@RequestParam("id") Integer id) {
      ModelAndView mav = new ModelAndView("editContact");
      Contact contact = contactsDAO.getById(id);
      mav.addObject("editContact", contact);
      return mav;
   }

   @RequestMapping(value="/updateContact", method = RequestMethod.POST)
   public String update(@ModelAttribute("editContact") Contact contact,
                        BindingResult result,
                        SessionStatus status) {
      validator.validate(contact, result);
      if (result.hasErrors()) {
         return "editContact";
      }
      contactsDAO.update(contact);
      status.setComplete();
      return "redirect:viewAllContacts.do";
   }

   @RequestMapping("deleteContact")
   public ModelAndView delete(@RequestParam("id") Integer id) {
      ModelAndView mav = new ModelAndView("redirect:viewAllContacts.do");
      contactsDAO.delete(id);
      return mav;
   }
}
