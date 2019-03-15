package org.fedster.bullhorn.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class BullhornController {

    @Autowired
    BullhornRepository bullhornRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listBullhorns(Model model) {
        model.addAttribute("bullhorns", bullhornRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String bullhornForm(Model model) {
        model.addAttribute("bullhorn", new Bullhorn());
        return "form";
    }

  /*  @PostMapping("/process")
    public String processForm(@Valid Bullhorn bullhorn, BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        }
        bullhornRepository.save(bullhorn);
        return "redirect:/";
    }*/

    @RequestMapping("/detail/{id}")
    public String show(@PathVariable("id") long id, Model model) {
        model.addAttribute("bullhorn", bullhornRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String update(@PathVariable("id") long id, Model model) {
        model.addAttribute("bullhorn", bullhornRepository.findById(id).get());
        return "form";
    }

    @RequestMapping("/delete/{id}")
    public String delBullhorn(@PathVariable("id") long id) {
        bullhornRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/process")
    public String processImage(@ModelAttribute @Valid Bullhorn bullhorn, BindingResult result, @RequestParam("file")MultipartFile file)
    {
        if (result.hasErrors() || file.isEmpty()) {
            return "form";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            bullhorn.setHeadshot(uploadResult.get("url").toString());
            bullhornRepository.save(bullhorn);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/form";
        }
        return "redirect:/";
    }
}
