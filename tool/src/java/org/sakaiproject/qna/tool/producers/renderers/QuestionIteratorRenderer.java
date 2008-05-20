package org.sakaiproject.qna.tool.producers.renderers;

import org.sakaiproject.qna.model.QnaQuestion;
import org.sakaiproject.qna.tool.otp.QuestionIteratorHelper;
import org.sakaiproject.qna.tool.params.QuestionParams;
import org.sakaiproject.qna.tool.producers.QuestionsListProducer;
import org.sakaiproject.qna.tool.producers.QueuedQuestionProducer;
import org.sakaiproject.qna.tool.producers.ViewPrivateReplyProducer;
import org.sakaiproject.qna.tool.producers.ViewQuestionProducer;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInitBlock;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;


/**
 *	Iterator used in views for question/private reply/new questions
 *		
 */
public class QuestionIteratorRenderer {
	
	private QuestionIteratorHelper questionIteratorHelper;
	private MessageLocator messageLocator;
    
	private boolean secondary = false;
	
	public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
	
	public void setQuestionIteratorHelper(QuestionIteratorHelper questionIteratorHelper) {
		this.questionIteratorHelper = questionIteratorHelper;
	}
	
	/**
	 * 
	 * @param tofill
	 * @param divID
	 * @param current Current question
	 */
	public void makeQuestionIterator(UIContainer tofill, String divID, QnaQuestion current) {
		 if (secondary) {
			 makeSecondaryQuestionIterator(tofill, divID, current);
			 return;
		 } else {
			 secondary = true;
		 }
		
		 UIJointContainer iterator = new UIJointContainer(tofill,divID,"question-iterator:");
		 questionIteratorHelper.setCurrentQuestion(current);
		 
		 if (!questionIteratorHelper.isFirst()) {
			 UIInternalLink link = UIInternalLink.make(tofill, "previous-item",new QuestionParams(getResultViewID(questionIteratorHelper.getPrevious()),questionIteratorHelper.getPrevious().getId()));
			 UIMessage message = UIMessage.make(tofill, "previous-item-btn","qna.general.previous");
			 message.decorators = new DecoratorList(new UITooltipDecorator(UIMessage.make("qna.question-iterator.previous-tooltip",new Object[]{messageLocator.getMessage(questionIteratorHelper.getListTypeMessageKey())})));
			 UIInitBlock.make(tofill, "init-previous", "make_button_call_link", new Object[] {message, link});
		 } 
		 
		 UIInternalLink returnlink = UIInternalLink.make(tofill, "return-to-list",new SimpleViewParameters(QuestionsListProducer.VIEW_ID));
		 UIMessage returnMessage = UIMessage.make(tofill, "return-to-list-btn","qna.general.return-to-list");
		 UIInitBlock.make(tofill, "init-return-to-list", "make_button_call_link", new Object[] {returnMessage, returnlink});
		 
		 if (!questionIteratorHelper.isLast()) {
			 UIInternalLink link = UIInternalLink.make(tofill, "next-item",new QuestionParams(getResultViewID(questionIteratorHelper.getNext()),questionIteratorHelper.getNext().getId()));
			 UIMessage message = UIMessage.make(tofill, "next-item-btn","qna.general.next");
			 message.decorators = new DecoratorList(new UITooltipDecorator(UIMessage.make("qna.question-iterator.next-tooltip",new Object[]{messageLocator.getMessage(questionIteratorHelper.getListTypeMessageKey())})));
			 UIInitBlock.make(tofill, "init-next", "make_button_call_link", new Object[] {message.getFullID(), link.getFullID()});
		 } 
	 }
	
	/**
	 *	Creates secondary Question Iterator, with different ids 
	 */
	private void makeSecondaryQuestionIterator(UIContainer tofill, String divID, QnaQuestion current) {
		 UIJointContainer iterator = new UIJointContainer(tofill,divID,"question-iterator-secondary:");
		 questionIteratorHelper.setCurrentQuestion(current);
		 
		 if (!questionIteratorHelper.isFirst()) {
			 UIInternalLink link = UIInternalLink.make(tofill, "previous-item-secondary",new QuestionParams(getResultViewID(questionIteratorHelper.getPrevious()),questionIteratorHelper.getPrevious().getId()));
			 UIMessage message = UIMessage.make(tofill, "previous-item-btn-secondary","qna.general.previous");
			 message.decorators = new DecoratorList(new UITooltipDecorator(UIMessage.make("qna.question-iterator.previous-tooltip",new Object[]{messageLocator.getMessage(questionIteratorHelper.getListTypeMessageKey())})));
			 UIInitBlock.make(tofill, "init-previous-secondary", "make_button_call_link", new Object[] {message, link});
		 } 
		 
		 UIInternalLink returnlink = UIInternalLink.make(tofill, "return-to-list-secondary",new SimpleViewParameters(QuestionsListProducer.VIEW_ID));
		 UIMessage returnMessage = UIMessage.make(tofill, "return-to-list-btn-secondary","qna.general.return-to-list");
		 UIInitBlock.make(tofill, "init-return-to-list-secondary", "make_button_call_link", new Object[] {returnMessage, returnlink});
		 
		 if (!questionIteratorHelper.isLast()) {
			 UIInternalLink link = UIInternalLink.make(tofill, "next-item-secondary",new QuestionParams(getResultViewID(questionIteratorHelper.getNext()),questionIteratorHelper.getNext().getId()));
			 UIMessage message = UIMessage.make(tofill, "next-item-btn-secondary","qna.general.next");
			 message.decorators = new DecoratorList(new UITooltipDecorator(UIMessage.make("qna.question-iterator.next-tooltip",new Object[]{messageLocator.getMessage(questionIteratorHelper.getListTypeMessageKey())})));
			 UIInitBlock.make(tofill, "init-next-secondary", "make_button_call_link", new Object[] {message.getFullID(), link.getFullID()});
		 } 		
	}
	 	
		
	 private String getResultViewID(QnaQuestion question) {
		 if (question.isPublished()) {
			 return ViewQuestionProducer.VIEW_ID;
		 } else {
			 if (question.hasPrivateReplies()) {
				 return ViewPrivateReplyProducer.VIEW_ID;
			 } else {
				 return QueuedQuestionProducer.VIEW_ID;
			 }
		 }
	 }
}
