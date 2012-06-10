/*
 * Copyright (C) 2008-2011 by Simon Hefti. All rights reserved.
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * Initial Developer: Simon Hefti
 */
package ch.heftix.mailxel.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OverviewGrid<T> extends VerticalPanel {

	private List<T> envelopes = new ArrayList<T>();
	private CursoredList<T> cl = null;
	private Presenter<T> presenter = null;

	private FlexTable grid = new FlexTable();

	public static final int LABEL_ROW = 0;
	private int currentPage = 0;
	private int first_payload_row = 1;

	private MailServiceAsync mailxelService = null;

	public OverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {
		init(mailxelService, mailxelPanel);
	}

	private void init(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

		this.mailxelService = mailxelService;

		ScrollPanel sp = new ScrollPanel();
		sp.add(grid);
		add(sp);
	}

	public void setPresenter(Presenter<T> presenter) {
		this.presenter = presenter;

		presenter.setHeader(grid);
	}

	public void fillGrid(List<T> result) {

		envelopes = result;

		// clean all except header
		int rows = grid.getRowCount();
		for (int i = rows - 1; i >= first_payload_row; i--) {
			grid.removeRow(i);
		}

		int row = first_payload_row;

		for (final T envelope : result) {

			presenter.setRow(grid, row, envelope);
			row++;

		}

		cl = new CursoredList<T>(envelopes);
		cl.setCursorPosition(0);
	}

}
