package com.aix.mpagents.interfaces;

import com.aix.mpagents.models.Variant;

public interface VariantInterface {
    void onVariantAdd(Variant variant);
    void onVariantClick(Variant variant);
    void onVariantClick(int position, Variant variant);
    void onVariantDelete(Variant variant);
    void onVariantDelete(int position, Variant variant);
    void onVariantUpdate(Variant variant);
    void onVariantUpdate(int position, Variant variant);
    Variant getIsVariantDuplicate(String name);
}
