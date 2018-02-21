package com.arkin.kpi.quartz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.quartz.dao.DashboardDao;
import com.arkin.kpi.quartz.model.to.DashboardKpiTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.quartz.service.DashboardService;
import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.util.UtilException;


@Transactional
@Service
public class SessionDashboardServiceImpl implements SessionDashboardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDashboardServiceImpl.class);

	@Autowired
	private DashboardDao dashboardDao;

	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private UtilException utilException;

	@Override
	public Map<String, Object> getDashboardsSessions(Map<String, String> params) {

		Map<String, Object> mapSockets = new HashMap<String, Object>();

		try {
			List<DashboardKpiTo> dashboards = dashboardService.getDashboardKpiByTable(params);

			List<SessionSocketTo> sessionSockets = dashboardDao.getSessionSocket();

			for (SessionSocketTo sessionSocket : sessionSockets) {

				Map<String, Object> kpis = new HashMap<>();

				for (DashboardKpiTo dash : dashboards) {
					if (sessionSocket.getDashboardPath().equalsIgnoreCase(dash.getPathDashboard())) {
						kpis.put(dash.getKpiName(), dash.getIdKpiComponent());
					}
				}
				if (kpis.size() > 0) {
					mapSockets.put(sessionSocket.getIdSession(), kpis);
				}
			}
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}

		return mapSockets;
	}

}
