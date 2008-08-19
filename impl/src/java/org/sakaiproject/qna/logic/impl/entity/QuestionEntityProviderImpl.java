/***********************************************************************************
 * QuestionEntityProviderImpl.java
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.qna.logic.impl.entity;

import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.qna.logic.QuestionLogic;
import org.sakaiproject.qna.logic.entity.QuestionEntityProvider;

public class QuestionEntityProviderImpl implements QuestionEntityProvider, CoreEntityProvider, AutoRegisterEntityProvider {

	private QuestionLogic questionLogic;
	
	public void setQuestionLogic(QuestionLogic questionLogic) {
		this.questionLogic = questionLogic;
	}
		
	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}

	/**
	 * Check if entity exists 
	 * 
	 * @param id id to check
	 * @ return true if exists, false otherwise
	 */
	public boolean entityExists(String id) {
		return questionLogic.existsQuestion(id);
	}

}
