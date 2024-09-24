package com.eds.k8s.controller.sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/samples")
public class SampleController {

	@GetMapping(path = "/")
	public String getIndex() {
		// This returns a JSON or XML with the users
		return "samples/index";
	}

	@GetMapping(path = "/buttons")
	public String getButton() {
		// This returns a JSON or XML with the users
		return "samples/buttons";
	}

	@GetMapping(path = "/cards")
	public String getCards() {
		// This returns a JSON or XML with the users
		return "samples/cards";
	}

	@GetMapping(path = "/utilities-color")
	public String getColor() {
		// This returns a JSON or XML with the users
		return "samples/utilities-color";
	}

	@GetMapping(path = "/utilities-border")
	public String getBoarder() {
		// This returns a JSON or XML with the users
		return "samples/utilities-border";
	}

	@GetMapping(path = "/utilities-animation")
	public String getAnimation() {
		// This returns a JSON or XML with the users
		return "samples/utilities-animation";
	}
	
	@GetMapping(path = "/utilities-other")
	public String getOther() {
		// This returns a JSON or XML with the users
		return "samples/utilities-other";
	}
	
	@GetMapping(path = "/login")
	public String getLogin() {
		// This returns a JSON or XML with the users
		return "samples/login";
	}
	
	@GetMapping(path = "/register")
	public String getRegister() {
		// This returns a JSON or XML with the users
		return "samples/register";
	}
	
	@GetMapping(path = "/forgot-password")
	public String getPassword() {
		// This returns a JSON or XML with the users
		return "samples/forgot-password";
	}
	
	@GetMapping(path = "/404")
	public String getError() {
		// This returns a JSON or XML with the users
		return "samples/404";
	}
	
	@GetMapping(path = "/blank")
	public String getBlank() {
		// This returns a JSON or XML with the users
		return "samples/blank";
	}
	
	@GetMapping(path = "/charts")
	public String getCharts() {
		// This returns a JSON or XML with the users
		return "samples/charts";
	}
	
	@GetMapping(path = "/tables")
	public String getTables() {
		// This returns a JSON or XML with the users
		return "samples/tables";
	}
	
	@GetMapping(path = "/get-started")
	public ModelAndView getStarted() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/get-started");
		mav.addObject("stepId", 1);
		return mav;
	}
	
	@GetMapping(path = "/install-options")
	public ModelAndView getInstallOptions() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/install-options");
		mav.addObject("stepId", 2);
		return mav;
	}
	
	@GetMapping(path = "/confiem-hosts")
	public ModelAndView getConfiemHosts() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/confiem-hosts");
		mav.addObject("stepId", 3);
		return mav;
	}
	
	@GetMapping(path = "/select-version")
	public ModelAndView getSelectVersion() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/select-version");
		mav.addObject("stepId", 4);
		return mav;
	}
	
	@GetMapping(path = "/choose-services")
	public ModelAndView getChooseServices() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/choose-services");
		mav.addObject("stepId", 5);
		return mav;
	}
	
	@GetMapping(path = "/install-start")
	public ModelAndView getInstallStart() {
		// This returns a JSON or XML with the users
		ModelAndView mav = new ModelAndView();
		mav.setViewName("samples/install-start");
		mav.addObject("stepId", 6);
		return mav;
	}
	
	@GetMapping(path = "/dashboard")
	public String getDashboard() {
		// This returns a JSON or XML with the users
		return "samples/dashboard";
	}
	
}
