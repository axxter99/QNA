package org.sakaiproject.qna.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.qna.logic.CategoryLogic;
import org.sakaiproject.qna.logic.ExternalLogic;
import org.sakaiproject.qna.logic.OptionsLogic;
import org.sakaiproject.qna.logic.PermissionLogic;
import org.sakaiproject.qna.model.QnaCategory;
import org.sakaiproject.qna.model.QnaOptions;
import org.sakaiproject.qna.tool.otp.CategoryLocator;
import org.sakaiproject.qna.tool.otp.QuestionLocator;
import org.sakaiproject.qna.tool.producers.renderers.NavBarRenderer;
import org.sakaiproject.qna.tool.producers.renderers.SearchBarRenderer;

import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class AskQuestionProducer implements ViewComponentProducer, NavigationCaseReporter {

	public static final String VIEW_ID = "ask_question";

	public String getViewID() {
		return VIEW_ID;
	}

	private NavBarRenderer navBarRenderer;
	private SearchBarRenderer searchBarRenderer;
	private TextInputEvolver richTextEvolver;
	private OptionsLogic optionsLogic;
	private PermissionLogic permissionLogic;
	private ExternalLogic externalLogic;
	private CategoryLogic categoryLogic;

	public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
		this.navBarRenderer = navBarRenderer;
	}

	public void setSearchBarRenderer(SearchBarRenderer searchBarRenderer) {
		this.searchBarRenderer = searchBarRenderer;
	}

	public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
		this.richTextEvolver = richTextEvolver;
	}

	public void setOptionsLogic(OptionsLogic optionsLogic) {
		this.optionsLogic = optionsLogic;
	}

	public void setPermissionLogic(PermissionLogic permissionLogic) {
		this.permissionLogic = permissionLogic;
	}

	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	public void setCategoryLogic(CategoryLogic categoryLogic) {
		this.categoryLogic = categoryLogic;
	}

	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
		QnaOptions options = optionsLogic.getOptionsForLocation(externalLogic.getCurrentLocationId());

		String multipleBeanMediator = "MultipleBeanMediator";
		String questionLocator = "QuestionLocator";
		String questionOTP = questionLocator + "." + QuestionLocator.NEW_1;
		String categoryLocator = "CategoryLocator";
		String categoryOTP = categoryLocator + "." + CategoryLocator.NEW_1;

		navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
		searchBarRenderer.makeSearchBar(tofill, "searchTool:", VIEW_ID);

		UIMessage.make(tofill, "page-title", "qna.ask-question.title");


		if (options.getAnonymousAllowed()) {
			UIMessage.make(tofill,"anonymous-note","qna.ask-question.anonymous-note");
		}

		UIMessage.make(tofill, "question-title", "qna.ask-question.question");

		UIForm form = UIForm.make(tofill, "ask-question-form");

		UIInput questiontext = UIInput.make(form, "question-input:",questionOTP +".questionText");
        richTextEvolver.evolveTextInput(questiontext);

        UIBoundBoolean.make(form,"answer-notify",questionOTP + ".notify",true);
        UIMessage.make(form,"answer-notify-label","qna.ask-question.notify-on-answer");

        if (!options.isModerated() || 
        	permissionLogic.canUpdate(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) || 
        	permissionLogic.canAddNewCategory(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId())) {
	        UIMessage.make(form, "category-title", "qna.ask-question.category");
	        UIMessage.make(form, "category-text", "qna.ask-question-select-category");
        }

        List<QnaCategory> categories = categoryLogic.getCategoriesForLocation(externalLogic.getCurrentLocationId());

        String[] categoriesIds = new String[categories.size()];
        String[] categoriesText = new String[categories.size()];

        for (int i=0;i<categories.size();i++) {
        	QnaCategory category = categories.get(i);
        	categoriesIds[i] = category.getId();
        	categoriesText[i] = category.getCategoryText();
        }
        
        boolean displayOr = false;
        if (permissionLogic.canUpdate(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) || !options.isModerated()) {
        	UISelect.make(form, "category-select", categoriesIds, categoriesText, questionOTP + ".categoryId" ); 
        	displayOr = true;
        }

        if (permissionLogic.canAddNewCategory(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId())) {
        	if (displayOr) {
        		UIMessage.make(form,"or","qna.general.or"); }
        	UIMessage.make(form,"new-category-label","qna.ask-question.create-category");
        	UIInput.make(form, "new-category-name", categoryOTP + ".categoryText");
        }

        UIMessage.make(form,"attachments-title","qna.ask-question.attachments");
       // UIMessage.make(form,"no-attachments-msg","qna.ask-question.no-attachments");
        UIMessage.make(form,"add-attachment-button","qna.ask-question.add-attachment");


        if (!permissionLogic.canUpdate(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) &&
        	options.isModerated()) {
        	UIMessage.make(form,"moderated-note","qna.ask-question.moderated-note");
        }

        UICommand command = UICommand.make(form,"add-question-button",UIMessage.make("qna.ask-question.add-question"),multipleBeanMediator + ".saveNew");
        UICommand.make(form,"cancel-button",UIMessage.make("qna.general.cancel")).setReturn("cancel");
	}

	public List reportNavigationCases() {
		List<NavigationCase> list = new ArrayList<NavigationCase>();
		list.add(new NavigationCase("cancel", new SimpleViewParameters(
				QuestionsListProducer.VIEW_ID)));
		return list;
	}
}
