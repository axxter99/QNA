/**
 * Copyright (c) 2007-2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.qna.logic.impl.sms;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.qna.comparators.AnswersListComparator;
import org.sakaiproject.qna.logic.ExternalLogic;
import org.sakaiproject.qna.logic.OptionsLogic;
import org.sakaiproject.qna.logic.PermissionLogic;
import org.sakaiproject.qna.logic.QnaBundleLogic;
import org.sakaiproject.qna.logic.QuestionLogic;
import org.sakaiproject.qna.model.QnaAnswer;
import org.sakaiproject.qna.model.QnaOptions;
import org.sakaiproject.qna.model.QnaQuestion;
import org.sakaiproject.qna.utils.TextUtil;
import org.sakaiproject.sms.logic.incoming.ParsedMessage;
import org.sakaiproject.sms.logic.incoming.ShortMessageCommand;

/**
 * Return the answers of a specific question in a specific Sakai site. Usage:
 * ANSWER <question nr>
 * 
 * @author wilhelm@psybergate.co.za
 * 
 */
public class AnswerSmsCommand implements ShortMessageCommand {

	private static Log log = LogFactory.getLog(AnswerSmsCommand.class);
	private static final String ANSWER = "ANSWER";
	private static final String ANSWER_ALIAS = "A";

	private QuestionLogic questionLogic;
	private QnaBundleLogic qnaBundleLogic;
	private PermissionLogic permissionLogic;
	private OptionsLogic optionsLogic;
	private ExternalLogic externalLogic;
	
	public void setQuestionLogic(QuestionLogic questionLogic) {
		this.questionLogic = questionLogic;
	}

	public void setQnaBundleLogic(QnaBundleLogic qnaBundleLogic) {
		this.qnaBundleLogic = qnaBundleLogic;
	}

	public void setPermissionLogic(PermissionLogic permissionLogic) {
		this.permissionLogic = permissionLogic;
	}

	public void setOptionsLogic(OptionsLogic optionsLogic) {
		this.optionsLogic = optionsLogic;
	}

	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	public String execute(ParsedMessage message, String messageType, String mobileNr) {
		
		String userId = message.getIncomingUserId();
		String body[] = message.getBodyParameters();

		log.debug(getCommandKey() + " command called with parameters: " + message);

		if (body == null || body.length == 0 || body[0] == null || "".equals(body[0].trim())) {
			return qnaBundleLogic.getString("qna.sms.no-question-id");
		} else {
			try {
				QnaQuestion question = questionLogic.getQuestionById(Long.valueOf(body[0]
						.trim()), userId, true);
				if (question == null) {
					return qnaBundleLogic
							.getString("qna.sms.invalid-question-id");
				} else {
					String siteRef = question.getLocation();
					message.setSite(externalLogic.getSiteIdFromRef(siteRef));
					
					log.debug("Location for question " + question.getId() + " is " + siteRef);
					
					List<QnaAnswer> answers = question.getAnswers();
					if (answers.size() == 0) {
						return qnaBundleLogic
								.getString("qna.sms.no-answers-found");
					} else {
						QnaOptions options = optionsLogic
								.getOptionsForLocation(siteRef);

						if (options.getMobileAnswersNr() > 0) {
							Collections.sort(answers,
									new AnswersListComparator(permissionLogic,
											siteRef));
							String smsReply = "";

							for (int i = 0; i < options.getMobileAnswersNr(); i++) {
								if (i < answers.size()) {
									smsReply += TextUtil.stripTags(answers.get(
											i).getAnswerText());
									if ((i + 1) < options.getMobileAnswersNr()
											&& (i + 1) < answers.size()) {
										smsReply += ", ";
									}
								}
							}
							return smsReply; // SMS tool will do truncation if
												// necessary
						} else {
							return qnaBundleLogic.getFormattedMessage(
									"qna.sms.no-mobile-answers",
									new Object[] { siteRef });
						}

					}
				}
			} catch (SecurityException se) {
				return qnaBundleLogic.getFormattedMessage(
						"qna.sms.read-denied", new Object[] { userId, body[0] });
			}
		}
	}

	public String[] getAliases() {
		return new String[] { ANSWER_ALIAS };
	}

	public String getCommandKey() {
		return ANSWER;
	}

	public String getHelpMessage(String messageType) {
		return qnaBundleLogic.getString("qna.sms.answer-help");
	}

	public int getBodyParameterCount() {
		return 1;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

	public boolean requiresSiteId() {
		return false;
	}

	public boolean canExecute(ParsedMessage message) {
		return true;
	}

}
