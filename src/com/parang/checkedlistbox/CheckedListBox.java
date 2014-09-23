package com.parang.checkedlistbox;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CheckedListBox extends LinearLayout {
	Context context;
	CheckedListBoxItem[] listItems;
	CheckBox[] checkBoxes;
	LinearLayout containerLinearLayout;
	TextView titleTextView;
	Typeface typeFace = null;
	String title = null;
	boolean showSelectAll;
	int startIndex;
	boolean compactMode = false;

	public CheckedListBox(Context context, AttributeSet attr) {
		super(context, attr);
		initialize(context);
	}

	public CheckedListBox(Context context) {
		super(context);
		initialize(context);
	}

	public CheckedListBox(Context context, AttributeSet attr, CheckedListBoxItem[] listItems) {
		super(context, attr);
		this.listItems = listItems;
		initialize(context);
	}

	public CheckedListBox(Context context, CheckedListBoxItem[] listItems) {
		super(context);
		this.listItems = listItems;
		initialize(context);
	}

	private void initialize(Context context) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.checked_list_box, this);

		this.context = context;

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		containerLinearLayout = (LinearLayout) findViewById(R.id.containerLinearLayout);

		titleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isCompactMode())
					return;

				if (containerLinearLayout.getVisibility() == View.GONE)
					containerLinearLayout.setVisibility(View.VISIBLE);
				else
					containerLinearLayout.setVisibility(View.GONE);
			}
		});

		startIndex = 0;

		refreshList();
	}

	private void refreshList() {
		if (!isCompactMode())
			containerLinearLayout.setVisibility(View.VISIBLE);

		if (listItems == null) {
			checkBoxes = null;
			containerLinearLayout.removeAllViews();
		} else {
			int length = listItems.length;

			if (showSelectAll) {
				length++;
				startIndex = 1;
			} else
				startIndex = 0;

			checkBoxes = null;
			containerLinearLayout.removeAllViews();
			checkBoxes = new CheckBox[length];

			if (showSelectAll) {
				checkBoxes[0] = new CheckBox(context);
				checkBoxes[0].setText(context.getString(R.string.All));
				checkBoxes[0].setTag(null);
				checkBoxes[0].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				if (typeFace != null)
					checkBoxes[0].setTypeface(typeFace);

				checkBoxes[0].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setSelectAllChecked(checkBoxes[0].isChecked());
					}
				});

				containerLinearLayout.addView(checkBoxes[0]);
			}

			for (int i = 0; i < listItems.length; i++) {
				checkBoxes[startIndex + i] = new CheckBox(context);
				checkBoxes[startIndex + i].setId(i);
				checkBoxes[startIndex + i].setText(listItems[i].getText());
				checkBoxes[startIndex + i].setTag(listItems[i].getValue());
				checkBoxes[startIndex + i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				if (typeFace != null)
					checkBoxes[startIndex + i].setTypeface(typeFace);

				checkBoxes[startIndex + i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						CheckBox checkBox = (CheckBox) view;
						setCheckedAt(checkBox.getId(), checkBox.isChecked());
					}
				});

				containerLinearLayout.addView(checkBoxes[startIndex + i]);
			}
		}
	}

	public CheckedListBoxItem[] getListItems() {
		return listItems;
	}

	public void setListItems(CheckedListBoxItem[] listItems) {
		this.listItems = listItems;
		refreshList();
	}

	public Typeface getTypeFace() {
		return typeFace;
	}

	public void setTypeFace(Typeface typeFace) {
		this.typeFace = typeFace;
		if (typeFace != null) {
			titleTextView.setTypeface(typeFace);

			if (checkBoxes != null) {
				for (int i = 0; i < checkBoxes.length; i++)
					checkBoxes[i].setTypeface(typeFace);
			}
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		if (title != null) {
			titleTextView.setText(title);
			titleTextView.setVisibility(VISIBLE);
		} else {
			titleTextView.setText(context.getString(R.string.TapHere));
			if (!isCompactMode())
				titleTextView.setVisibility(GONE);
		}
	}

	public boolean getShowSelectAll() {
		return showSelectAll;
	}

	public void setShowSelectAll(boolean showSelectAll) {
		this.showSelectAll = showSelectAll;
		refreshList();
	}

	public void setSelectAllChecked(boolean checked) {
		if (checkBoxes == null)
			return;

		if (showSelectAll) {
			for (int i = 0; i < checkBoxes.length; i++)
				checkBoxes[i].setChecked(checked);
		}
	}

	public boolean isSelectAllChecked() {
		if (checkBoxes == null)
			return false;

		if (showSelectAll)
			return checkBoxes[0].isChecked();
		else
			return false;
	}

	public int getItemPosition(Object value) {
		if (checkBoxes == null)
			return -1;

		for (int i = 0; i < listItems.length; i++) {
			if (listItems[i].getValue().equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public void setCheckedAt(int position, boolean checked) {
		if (checkBoxes == null)
			return;

		checkBoxes[startIndex + position].setChecked(checked);
		if (showSelectAll) {
			if (checked == false)
				checkBoxes[0].setChecked(false);
			else {
				boolean allChecked = true;
				for (int i = startIndex; i < checkBoxes.length; i++) {
					if (checkBoxes[i].isChecked() == false) {
						allChecked = false;
						break;
					}
				}
				checkBoxes[0].setChecked(allChecked);
			}
		}
	}

	public void setChecked(Object value, boolean checked) {
		if (checkBoxes == null)
			return;

		int position = getItemPosition(value);
		if (position >= 0)
			setCheckedAt(position, checked);
	}

	public boolean getCheckedAt(int position) {
		if (checkBoxes == null)
			return false;

		return checkBoxes[startIndex + position].isChecked();
	}

	public boolean getChecked(Object value) {
		if (checkBoxes == null)
			return false;

		int position = getItemPosition(value);
		if (position >= 0)
			return getCheckedAt(position);
		return false;
	}

	public Integer[] getCheckedPositions() {
		List<Integer> selections = new ArrayList<Integer>();

		for (int i = startIndex; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked())
				selections.add(i - startIndex);
		}

		return selections.toArray(new Integer[selections.size()]);
	}

	public Object[] getCheckedValues() {
		List<Object> selections = new ArrayList<Object>();

		for (int i = startIndex; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked())
				selections.add(checkBoxes[i].getTag());
		}

		return selections.toArray();
	}

	public int getCheckedCount() {
		int checked = 0;

		for (int i = startIndex; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked())
				checked++;
		}
		return checked;
	}

	public boolean isCompactMode() {
		return compactMode;
	}

	public void setCompactMode(boolean compactMode) {
		this.compactMode = compactMode;
		setTitle(title);
	}

	public boolean isListOpened() {
		return containerLinearLayout.getVisibility() == View.VISIBLE;
	}

	public void setListOpened(boolean listOpened) {
		if (!isCompactMode())
			return;

		if (listOpened)
			containerLinearLayout.setVisibility(View.VISIBLE);
		else
			containerLinearLayout.setVisibility(View.GONE);
	}

}