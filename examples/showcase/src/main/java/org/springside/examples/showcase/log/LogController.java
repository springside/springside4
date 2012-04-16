package org.springside.examples.showcase.log;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.log.Log4jManager;

import com.google.common.collect.Lists;

@Controller
@RequestMapping("/log")
public class LogController {

	private List levels = Lists.newArrayList("", "ERROR", "WARN", "INFO");

	@Autowired
	private Log4jManager log4jManager;

	@RequestMapping(value = "/console")
	public String console(Model model) {

		createFormObject(model);

		return "log/console";
	}

	@RequestMapping(value = "/defaultsetting")
	public String updateDefaultLogger(FormObject command, Model model) {
		log4jManager.setRootLoggerLevel(command.getRootLoggerLevel());
		log4jManager.setProjectLoggerLevel(command.getProjectLoggerLevel());

		createFormObject(model);

		return "log/console";
	}

	@RequestMapping(value = "/loggersetting")
	public String updateLogger(FormObject command, Model model) {
		String loggerName = command.loggerName;
		String loggerLevel = null;
		if (StringUtils.isNotBlank(command.loggerLevel)) {
			loggerLevel = command.loggerLevel;
			log4jManager.setLoggerLevel(command.loggerName, command.loggerLevel);

		} else {
			loggerLevel = log4jManager.getLoggerLevel(command.loggerName);
		}

		FormObject newCommand = createFormObject(model);
		newCommand.loggerName = loggerName;
		newCommand.loggerLevel = loggerLevel;
		return "log/console";
	}

	private FormObject createFormObject(Model model) {
		FormObject command = new FormObject();
		command.rootLoggerLevel = log4jManager.getRootLoggerLevel();
		command.projectLoggerLevel = log4jManager.getProjectLoggerLevel();
		model.addAttribute("command", command);

		model.addAttribute("levels", levels);
		return command;
	}

	public static class FormObject {
		private String rootLoggerLevel;
		private String projectLoggerLevel;
		private String loggerName;
		private String loggerLevel;

		public String getRootLoggerLevel() {
			return rootLoggerLevel;
		}

		public String getProjectLoggerLevel() {
			return projectLoggerLevel;
		}

		public String getLoggerName() {
			return loggerName;
		}

		public String getLoggerLevel() {
			return loggerLevel;
		}

		public void setRootLoggerLevel(String rootLoggerLevel) {
			this.rootLoggerLevel = rootLoggerLevel;
		}

		public void setProjectLoggerLevel(String projectLoggerLevel) {
			this.projectLoggerLevel = projectLoggerLevel;
		}

		public void setLoggerName(String loggerName) {
			this.loggerName = loggerName;
		}

		public void setLoggerLevel(String loggerLevel) {
			this.loggerLevel = loggerLevel;
		}

	}
}
