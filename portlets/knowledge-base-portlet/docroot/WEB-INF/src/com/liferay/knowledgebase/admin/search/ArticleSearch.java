/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.knowledgebase.admin.search;

import com.liferay.knowledgebase.model.Article;
import com.liferay.knowledgebase.util.PortletKeys;
import com.liferay.knowledgebase.util.comparator.ArticleCreateDateComparator;
import com.liferay.knowledgebase.util.comparator.ArticleModifiedDateComparator;
import com.liferay.knowledgebase.util.comparator.ArticlePriorityComparator;
import com.liferay.knowledgebase.util.comparator.ArticleStatusComparator;
import com.liferay.knowledgebase.util.comparator.ArticleTitleComparator;
import com.liferay.knowledgebase.util.comparator.ArticleUserNameComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.PortalPreferences;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
public class ArticleSearch extends SearchContainer<Article> {

	static List<String> headerNames = new ArrayList<String>();
	static Map<String, String> orderableHeaders = new HashMap<String, String>();

	static {
		headerNames.add("priority");
		headerNames.add("title");
		headerNames.add("author");
		headerNames.add("create-date");
		headerNames.add("modified-date");
		headerNames.add("status");

		orderableHeaders.put("priority", "priority");
		orderableHeaders.put("title", "title");
		orderableHeaders.put("author", "user-name");
		orderableHeaders.put("create-date", "create-date");
		orderableHeaders.put("modified-date", "modified-date");
		orderableHeaders.put("status", "status");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "no-articles-were-found";

	public ArticleSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new ArticleDisplayTerms(portletRequest),
			new ArticleSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		ArticleDisplayTerms displayTerms =
			(ArticleDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			ArticleDisplayTerms.ANYTIME,
			String.valueOf(displayTerms.isAnytime()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.CONTENT, displayTerms.getContent());
		iteratorURL.setParameter(
			ArticleDisplayTerms.END_DATE_DAY,
			String.valueOf(displayTerms.getEndDateDay()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.END_DATE_MONTH,
			String.valueOf(displayTerms.getEndDateMonth()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.END_DATE_YEAR,
			String.valueOf(displayTerms.getEndDateYear()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.START_DATE_DAY,
			String.valueOf(displayTerms.getStartDateDay()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.START_DATE_MONTH,
			String.valueOf(displayTerms.getStartDateMonth()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.START_DATE_YEAR,
			String.valueOf(displayTerms.getStartDateYear()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.STATUS,
			String.valueOf(displayTerms.getStatus()));
		iteratorURL.setParameter(
			ArticleDisplayTerms.TITLE, displayTerms.getTitle());

		try {
			PortalPreferences preferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					portletRequest);

			String oldOrderByCol = preferences.getValue(
				PortletKeys.KNOWLEDGE_BASE_ADMIN, "articles-order-by-col",
				"modified-date");
			String oldOrderByType = preferences.getValue(
				PortletKeys.KNOWLEDGE_BASE_ADMIN, "articles-order-by-type",
				"desc");

			String orderByCol = ParamUtil.getString(
				portletRequest, "orderByCol", oldOrderByCol);
			String orderByType = ParamUtil.getString(
				portletRequest, "orderByType", oldOrderByType);

			if (!Validator.equals(orderByCol, oldOrderByCol) ||
				!Validator.equals(orderByType, oldOrderByType)) {

				preferences.setValue(
					PortletKeys.KNOWLEDGE_BASE_ADMIN, "articles-order-by-col",
					orderByCol);
				preferences.setValue(
					PortletKeys.KNOWLEDGE_BASE_ADMIN, "articles-order-by-type",
					orderByType);

				ArticleSearchTerms searchTerms =
					(ArticleSearchTerms)getSearchTerms();

				searchTerms.setCurStartValues(new int[0]);
			}

			boolean orderByAsc = false;

			if (orderByType.equals("asc")) {
				orderByAsc = true;
			}

			OrderByComparator orderByComparator = null;

			if (orderByCol.equals("create-date")) {
				orderByComparator = new ArticleCreateDateComparator(orderByAsc);
			}
			else if (orderByCol.equals("modified-date")) {
				orderByComparator = new ArticleModifiedDateComparator(
					orderByAsc);
			}
			else if (orderByCol.equals("priority")) {
				orderByComparator = new ArticlePriorityComparator(orderByAsc);
			}
			else if (orderByCol.equals("status")) {
				orderByComparator = new ArticleStatusComparator(orderByAsc);
			}
			else if (orderByCol.equals("title")) {
				orderByComparator = new ArticleTitleComparator(orderByAsc);
			}
			else if (orderByCol.equals("user-name")) {
				orderByComparator = new ArticleUserNameComparator(orderByAsc);
			}

			setOrderableHeaders(orderableHeaders);
			setOrderByCol(orderByCol);
			setOrderByType(orderByType);
			setOrderByComparator(orderByComparator);
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ArticleSearch.class);

}