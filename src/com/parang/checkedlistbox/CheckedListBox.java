package com.parang.checkedlistbox;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CheckedListBox extends LinearLayout {
	private Context context;
	private CheckedListBoxItem[] listItems;
	private CheckBox[] checkBoxes;
	private LinearLayout containerLinearLayout;
	private TextView titleTextView;
	private Typeface typeFace = null;
	private String title = null;
	private boolean showSelectAll;
	private int startIndex;
	private boolean compactMode = false;
	private int checkBoxWidth = LayoutParams.MATCH_PARENT, checkBoxHeight = LayoutParams.WRAP_CONTENT, checkBoxMarginLeft = 0, checkBoxMarginTop = 0, checkBoxMarginRight = 0, checkBoxMarginBottom = 0;
	private int dividerWidth = LayoutParams.MATCH_PARENT, dividerHeight = LayoutParams.WRAP_CONTENT, dividerMarginLeft = 0, dividerMarginTop = 0, dividerMarginRight = 0, dividerMarginBottom = 0;
	private int titleDividerWidth = LayoutParams.MATCH_PARENT, titleDividerHeight = LayoutParams.WRAP_CONTENT, titleDividerMarginLeft = 0, titleDividerMarginTop = 0, titleDividerMarginRight = 0, titleDividerMarginBottom = 0;
	private int titleWidth = LayoutParams.MATCH_PARENT, titleHeight = LayoutParams.WRAP_CONTENT, titleMarginLeft = 0, titleMarginTop = 0, titleMarginRight = 0, titleMarginBottom = 0;
	private Drawable checkBoxBackgroundDrawable = null, dividerBackgroundDrawable = null, titleDividerBackgroundDrawable = null, titleBackgroundDrawable = null, expandDrawable, collapseDrawable, iconDrawable;
	private LinearLayout.LayoutParams checkBoxLayoutParams, dividerLayoutParams, titleDividerLayoutParams, titleLayoutParams, textViewLayoutParams;
	private int titleTextColor;
	private View titleDividerView;
	private int direction;
	
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

		String directionString = context.getString(R.string.Direction);
		if(directionString.equals("rtl"))
			direction = 1;
		else
			direction = 0;
		
		textViewLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
		
		checkBoxLayoutParams = new LayoutParams(checkBoxWidth, checkBoxHeight);
		checkBoxLayoutParams.setMargins(checkBoxMarginLeft, checkBoxMarginTop, checkBoxMarginRight, checkBoxMarginBottom);

		dividerLayoutParams = new LayoutParams(dividerWidth, dividerHeight);
		dividerLayoutParams.setMargins(dividerMarginLeft, dividerMarginTop, dividerMarginRight, dividerMarginBottom);
		
		titleLayoutParams = new LayoutParams(titleWidth, titleHeight);
		titleLayoutParams.setMargins(titleMarginLeft, titleMarginTop, titleMarginRight, titleMarginBottom);
		
		titleDividerLayoutParams = new LayoutParams(titleDividerWidth, titleDividerHeight);
		titleDividerLayoutParams.setMargins(titleDividerMarginLeft, titleDividerMarginTop, titleDividerMarginRight, titleDividerMarginBottom);
		
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		containerLinearLayout = (LinearLayout) findViewById(R.id.containerLinearLayout);
		titleDividerView = findViewById(R.id.titleDividerView);

		titleDividerView.setLayoutParams(titleDividerLayoutParams);
		titleTextView.setLayoutParams(titleLayoutParams);
		
		titleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isCompactMode())
					return;

				if (containerLinearLayout.getVisibility() == View.GONE)
					containerLinearLayout.setVisibility(View.VISIBLE);
				else
					containerLinearLayout.setVisibility(View.GONE);
				
				if(isCompactMode()){
					if(containerLinearLayout.getVisibility() == View.VISIBLE){
						if(direction == 0)
							titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, collapseDrawable, null);
						else
							titleTextView.setCompoundDrawablesWithIntrinsicBounds(collapseDrawable, null, iconDrawable, null);
					}
					else{
						if(direction == 0)
							titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, expandDrawable, null);
						else
							titleTextView.setCompoundDrawablesWithIntrinsicBounds(expandDrawable, null, iconDrawable, null);
					}
				}
				else{
					if(direction == 0)
						titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
					else
						titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, iconDrawable, null);
				}
			}
		});

		startIndex = 0;

		refreshList();
	}

	@SuppressWarnings("deprecation")
	private void refreshList() {
		Object[] selectedValues = getCheckedValues();
		View divider;

		if (!isCompactMode())
			containerLinearLayout.setVisibility(View.VISIBLE);

		if(isCompactMode()){
			if(containerLinearLayout.getVisibility() == View.VISIBLE){
				if(direction == 0)
					titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, collapseDrawable, null);
				else
					titleTextView.setCompoundDrawablesWithIntrinsicBounds(collapseDrawable, null, iconDrawable, null);
			}
			else{
				if(direction == 0)
					titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, expandDrawable, null);
				else
					titleTextView.setCompoundDrawablesWithIntrinsicBounds(expandDrawable, null, iconDrawable, null);
			}
		}
		else{
			if(direction == 0)
				titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
			else
				titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, iconDrawable, null);
		}
		
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
				LinearLayout checkBoxLinearLayout = new LinearLayout(context);
				checkBoxLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				checkBoxLinearLayout.setLayoutParams(checkBoxLayoutParams);
				
				TextView checkBoxTextView = new TextView(context);
				checkBoxTextView.setText(context.getString(R.string.All));
				checkBoxTextView.setLayoutParams(textViewLayoutParams);
				
				checkBoxes[0] = new CheckBox(context);
				checkBoxes[0].setText("");
				checkBoxes[0].setTag(null);
				checkBoxes[0].setBackgroundDrawable(checkBoxBackgroundDrawable);

				if (typeFace != null){
					checkBoxes[0].setTypeface(typeFace);
					checkBoxTextView.setTypeface(typeFace);
				}

				checkBoxes[0].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setSelectAllChecked(checkBoxes[0].isChecked());
					}
				});

				checkBoxTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						checkBoxes[0].performClick();
					}
				});
				
				if(direction == 0){
					checkBoxLinearLayout.setGravity(Gravity.LEFT);
					checkBoxLinearLayout.addView(checkBoxes[0]);
					checkBoxLinearLayout.addView(checkBoxTextView);
					checkBoxTextView.setGravity(Gravity.RIGHT);
				}
				else{
					checkBoxLinearLayout.setGravity(Gravity.RIGHT);
					checkBoxLinearLayout.addView(checkBoxTextView);
					checkBoxLinearLayout.addView(checkBoxes[0]);
					checkBoxTextView.setGravity(Gravity.LEFT);
				}

				containerLinearLayout.addView(checkBoxLinearLayout);

				if (dividerBackgroundDrawable != null) {
					divider = new View(context);
					divider.setLayoutParams(dividerLayoutParams);
					divider.setBackgroundDrawable(dividerBackgroundDrawable);
					containerLinearLayout.addView(divider);
				}
			}

			for (int i = 0; i < listItems.length; i++) {
				LinearLayout checkBoxLinearLayout = new LinearLayout(context);
				checkBoxLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				checkBoxLinearLayout.setLayoutParams(checkBoxLayoutParams);
				
				TextView checkBoxTextView = new TextView(context);
				checkBoxTextView.setText(listItems[i].getText());
				checkBoxTextView.setLayoutParams(textViewLayoutParams);
				checkBoxTextView.setId(i);
				
				checkBoxes[startIndex + i] = new CheckBox(context);
				checkBoxes[startIndex + i].setId(i);
				checkBoxes[startIndex + i].setText("");
				checkBoxes[startIndex + i].setTag(listItems[i].getValue());
				checkBoxes[startIndex + i].setBackgroundDrawable(checkBoxBackgroundDrawable);

				if (typeFace != null){
					checkBoxTextView.setTypeface(typeFace);
					checkBoxes[startIndex + i].setTypeface(typeFace);
				}

				checkBoxes[startIndex + i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						CheckBox checkBox = (CheckBox) view;
						setCheckedAt(checkBox.getId(), checkBox.isChecked());
					}
				});

				checkBoxTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						checkBoxes[startIndex + view.getId()].performClick();
					}
				});
				
				if(direction == 0){
					checkBoxLinearLayout.setGravity(Gravity.LEFT);
					checkBoxLinearLayout.addView(checkBoxes[startIndex + i]);
					checkBoxLinearLayout.addView(checkBoxTextView);
					checkBoxTextView.setGravity(Gravity.RIGHT);
				}
				else{
					checkBoxLinearLayout.setGravity(Gravity.RIGHT);
					checkBoxLinearLayout.addView(checkBoxTextView);
					checkBoxLinearLayout.addView(checkBoxes[startIndex + i]);
					checkBoxTextView.setGravity(Gravity.LEFT);
				}

				containerLinearLayout.addView(checkBoxLinearLayout);

				if (dividerBackgroundDrawable != null) {
					divider = new View(context);
					divider.setLayoutParams(dividerLayoutParams);
					divider.setBackgroundDrawable(dividerBackgroundDrawable);
					containerLinearLayout.addView(divider);
				}
			}

			for (int i = 0; i < selectedValues.length; i++) {
				setChecked(selectedValues[i], true);
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
		if (checkBoxes == null)
			return new Integer[0];

		List<Integer> selections = new ArrayList<Integer>();

		for (int i = startIndex; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked())
				selections.add(i - startIndex);
		}

		return selections.toArray(new Integer[selections.size()]);
	}

	public Object[] getCheckedValues() {
		if (checkBoxes == null)
			return new Object[0];

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
		if (isCompactMode())
			containerLinearLayout.setVisibility(View.GONE);
		else
			containerLinearLayout.setVisibility(View.VISIBLE);
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

	public void setCheckBoxSize(int width, int height) {
		checkBoxWidth = width;
		checkBoxHeight = height;

		checkBoxLayoutParams = new LayoutParams(checkBoxWidth, checkBoxHeight);
		checkBoxLayoutParams.setMargins(checkBoxMarginLeft, checkBoxMarginTop, checkBoxMarginRight, checkBoxMarginBottom);

		refreshList();
	}

	public void setCheckBoxBackground(Drawable background) {
		checkBoxBackgroundDrawable = background;

		refreshList();
	}

	public void setCheckBoxMargins(int left, int right, int top, int bottom) {
		checkBoxMarginLeft = left;
		checkBoxMarginTop = top;
		checkBoxMarginRight = right;
		checkBoxMarginBottom = bottom;

		checkBoxLayoutParams = new LayoutParams(checkBoxWidth, checkBoxHeight);
		checkBoxLayoutParams.setMargins(checkBoxMarginLeft, checkBoxMarginTop, checkBoxMarginRight, checkBoxMarginBottom);

		refreshList();
	}

	public void setDividerSize(int width, int height) {
		dividerWidth = width;
		dividerHeight = height;

		dividerLayoutParams = new LayoutParams(dividerWidth, dividerHeight);
		dividerLayoutParams.setMargins(dividerMarginLeft, dividerMarginTop, dividerMarginRight, dividerMarginBottom);

		refreshList();
	}

	public void setDividerBackground(Drawable background) {
		dividerBackgroundDrawable = background;

		refreshList();
	}

	public void setDividerMargins(int left, int right, int top, int bottom) {
		dividerMarginLeft = left;
		dividerMarginTop = top;
		dividerMarginRight = right;
		dividerMarginBottom = bottom;

		dividerLayoutParams = new LayoutParams(dividerWidth, dividerHeight);
		dividerLayoutParams.setMargins(dividerMarginLeft, dividerMarginTop, dividerMarginRight, dividerMarginBottom);

		refreshList();
	}

	public int getTitleTextColor() {
		return titleTextColor;
	}

	public void setTitleTextColor(int titleTextColor) {
		this.titleTextColor = titleTextColor;
		titleTextView.setTextColor(titleTextColor);
	}

	@SuppressWarnings("deprecation")
	public void setTitleBackground(Drawable background) {
		titleBackgroundDrawable = background;
		titleTextView.setBackgroundDrawable(titleBackgroundDrawable);
	}

	public void setTitleSize(int width, int height) {
		titleWidth = width;
		titleHeight = height;

		titleLayoutParams = new LayoutParams(titleWidth, titleHeight);
		titleLayoutParams.setMargins(titleMarginLeft, titleMarginTop, titleMarginRight, titleMarginBottom);

		titleTextView.setLayoutParams(titleLayoutParams);
	}

	public void setTitleMargins(int left, int right, int top, int bottom) {
		titleMarginLeft = left;
		titleMarginTop = top;
		titleMarginRight = right;
		titleMarginBottom = bottom;

		titleLayoutParams = new LayoutParams(titleWidth, titleHeight);
		titleLayoutParams.setMargins(titleMarginLeft, titleMarginTop, titleMarginRight, titleMarginBottom);

		titleTextView.setLayoutParams(titleLayoutParams);
	}
	
	public void setTitleDividerSize(int width, int height) {
		titleDividerWidth = width;
		titleDividerHeight = height;

		titleDividerLayoutParams = new LayoutParams(titleDividerWidth, titleDividerHeight);
		titleDividerLayoutParams.setMargins(titleDividerMarginLeft, titleDividerMarginTop, titleDividerMarginRight, titleDividerMarginBottom);

		titleDividerView.setLayoutParams(titleDividerLayoutParams);
	}

	public void setTitleDividerMargins(int left, int right, int top, int bottom) {
		titleDividerMarginLeft = left;
		titleDividerMarginTop = top;
		titleDividerMarginRight = right;
		titleDividerMarginBottom = bottom;

		titleDividerLayoutParams = new LayoutParams(titleDividerWidth, titleDividerHeight);
		titleDividerLayoutParams.setMargins(titleDividerMarginLeft, titleDividerMarginTop, titleDividerMarginRight, titleDividerMarginBottom);

		titleDividerView.setLayoutParams(titleDividerLayoutParams);
	}
	
	@SuppressWarnings("deprecation")
	public void setTitleDividerBackground(Drawable background) {
		titleDividerBackgroundDrawable = background;
		titleDividerView.setBackgroundDrawable(titleDividerBackgroundDrawable);

		if (background == null) {
			titleDividerView.setVisibility(View.GONE);
		} else {
			titleDividerView.setVisibility(View.VISIBLE);
		}
	}

	public Drawable getExpandDrawable() {
		return expandDrawable;
	}

	public void setExpandDrawable(Drawable expandDrawable) {
		this.expandDrawable = expandDrawable;
	}

	public Drawable getCollapseDrawable() {
		return collapseDrawable;
	}

	public void setCollapseDrawable(Drawable collapseDrawable) {
		this.collapseDrawable = collapseDrawable;
	}

	public Drawable getIconDrawable() {
		return iconDrawable;
	}

	public void setIconDrawable(Drawable iconDrawable) {
		this.iconDrawable = iconDrawable;
	}
}