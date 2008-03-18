/******************************************************************************
 * QnaDao.java - created by Sakai App Builder -AZ
 *
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 *
 * A copy of the Educational Community License has been included in this
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 *****************************************************************************/

package org.sakaiproject.qna.dao;

import java.util.List;

import org.sakaiproject.genericdao.api.CompleteGenericDao;
import org.sakaiproject.qna.model.QnaAnswer;

/**
 * This is a specialized DAO that allows the developer to extend the functionality of the generic dao package
*/

public interface QnaDao extends CompleteGenericDao {
	public List<QnaAnswer> getSearchAnswers(String search, String location);
}
