package com.aix.mpagents.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.aix.mpagents.R;


public class MenuUtils {

    @SuppressLint("RestrictedApi")
    public static void showMenuWithIcons(Context context, View view, PopupMenu.OnMenuItemClickListener menuOnClick){
        ContextThemeWrapper wraper = new ContextThemeWrapper(
                context,
                R.style.popupMenu
        );

        PopupMenu menu = new PopupMenu(wraper, view);
        menu.inflate(R.menu.product_options);
        menu.setOnMenuItemClickListener(menuOnClick);

        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) menu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }
}
