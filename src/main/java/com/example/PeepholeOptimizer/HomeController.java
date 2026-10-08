package com.example.PeepholeOptimizer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final CompilerService compilerService;

    public HomeController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("code") String code,
            Model model) {

        try {

            OptimizationResult result =
                    compilerService.analyze(code);

            model.addAttribute("code", code);
            model.addAttribute("before", result.getBefore());
            model.addAttribute("after", result.getAfter());
            model.addAttribute("beforeCount", result.getBeforeCount());
            model.addAttribute("afterCount", result.getAfterCount());
            model.addAttribute("arithmeticCount", result.getArithmeticCount());
            model.addAttribute(
                    "reduction",
                    String.format("%.2f", result.getReduction())
            );
            model.addAttribute(
                    "optimizations",
                    result.getOptimizations()
            );
            model.addAttribute(
                    "candidates",
                    result.getCandidates()
            );

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

        }

        return "index";
    }
}