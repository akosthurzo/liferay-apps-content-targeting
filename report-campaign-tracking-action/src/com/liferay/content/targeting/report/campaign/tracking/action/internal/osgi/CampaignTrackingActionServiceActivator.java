/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.content.targeting.report.campaign.tracking.action.internal.osgi;

import com.liferay.content.targeting.report.campaign.tracking.action.service.CampaignTrackingActionLocalService;
import com.liferay.osgi.util.service.ServiceTrackerUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.messaging.SerialDestination;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.context.ApplicationContext;

/**
 * @author Eduardo Garcia
 */
public class CampaignTrackingActionServiceActivator
	implements BundleActivator, MessageListener {

	@Override
	public synchronized void receive(Message message)
		throws MessageListenerException {

		ApplicationContext applicationContext =
			(ApplicationContext)message.getPayload();

		CampaignTrackingActionLocalService campaignTrackingActionLocalService =
			(CampaignTrackingActionLocalService)applicationContext.getBean(
				CampaignTrackingActionLocalService.class.getName());

		_campaignTrackingActionLocalServiceServiceRegistration =
			_bundleContext.registerService(
				CampaignTrackingActionLocalService.class,
				campaignTrackingActionLocalService, null);
	}

	@Override
	public synchronized void start(BundleContext bundleContext)
		throws Exception {

		_bundleContext = bundleContext;

		SerialDestination destination = new SerialDestination(DESTINATION_NAME);

		MessageBus messageBus = ServiceTrackerUtil.getService(
			MessageBus.class, bundleContext);

		messageBus.addDestination(destination);

		messageBus.registerMessageListener(DESTINATION_NAME, this);
	}

	@Override
	public synchronized void stop(BundleContext context) throws Exception {
		MessageBusUtil.removeDestination(DESTINATION_NAME);

		MessageBusUtil.unregisterMessageListener(DESTINATION_NAME, this);

		if (_campaignTrackingActionLocalServiceServiceRegistration != null) {
			_campaignTrackingActionLocalServiceServiceRegistration.unregister();

			_campaignTrackingActionLocalServiceServiceRegistration = null;
		}
	}

	protected static final String DESTINATION_NAME =
		"report-campaign-tracking-action-spring";

	private BundleContext _bundleContext;
	private ServiceRegistration<CampaignTrackingActionLocalService>
		_campaignTrackingActionLocalServiceServiceRegistration;

}