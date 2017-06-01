package com.ryan.bilibili_client.widget.sectioned;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by MUFCRyan on 2017/5/31.
 * A custom RecyclerView with Sections with custom Titles.
 * Sections are displayed in the same order they were added.
 */

public class SectionedRecyclerViewAdapter extends RecyclerView.Adapter {
    public final static int VIEW_TYPE_HEADER = 0;
    public final static int VIEW_TYPE_FOOTER = 1;
    public final static int VIEW_TYPE_ITEM_LOADED = 2;
    public final static int VIEW_TYPE_LOADING = 3;
    public final static int VIEW_TYPE_FAILED = 4;
    public final static int VIEW_TYPE_QTY = 5;

    private LinkedHashMap<String, Section> mSections;
    private HashMap<String, Integer> mSectionViewTypeNums;
    private int mViewTypeCount;

    public SectionedRecyclerViewAdapter(){
        mSections = new LinkedHashMap<>();
        mSectionViewTypeNums = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        for (Map.Entry<String, Integer> entry : mSectionViewTypeNums.entrySet()) {
            if (viewType >= entry.getValue() && viewType < entry.getValue() + VIEW_TYPE_QTY){
                Section section = mSections.get(entry.getKey());
                int sectionViewType = viewType - entry.getValue();
                switch(sectionViewType){
                    case VIEW_TYPE_HEADER: {
                        Integer resId = section.getHeaderResourceId();
                        if (resId == null)
                            throw new NullPointerException("Missing 'header' resource id");
                        view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                        // Get the header viewHolder from the section
                        viewHolder = section.getHeaderViewHolder(view);
                        break;
                    }
                    case VIEW_TYPE_FOOTER: {
                        Integer resId = section.getFooterResourceId();
                        if (resId == null)
                            throw new NullPointerException("Missing 'footer' resource id");
                        view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                        // Get the footer viewHolder from the section
                        viewHolder = section.getFooterViewHolder(view);
                        break;
                    }
                    case VIEW_TYPE_ITEM_LOADED: {
                        Integer resId = section.getItemResourceId();
                        view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                        // Get the item viewHolder from the section
                        viewHolder = section.getItemViewHolder(view);
                        break;
                    }
                    case VIEW_TYPE_LOADING: {
                        Integer resId = section.getLoadingResourceId();
                        if (resId == null)
                            throw new NullPointerException("Missing 'loading state' resource id");
                        view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                        // Get the loading state viewHolder from the section
                        viewHolder = section.getLoadingViewHolder(view);
                        break;
                    }
                    case VIEW_TYPE_FAILED: {
                        Integer resId = section.getFailedResourceId();
                        if (resId == null)
                            throw new NullPointerException("Missing 'failed state' resource id");
                        view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                        // Get the failed state viewHolder from the section
                        viewHolder = section.getFailedViewHolder(view);
                        break;
                    }
                    default :
                        throw new IllegalArgumentException("Invalid viewType");
                }
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int currentPosition = 0;
        for (Map.Entry<String, Section> entry : mSections.entrySet()) {
            Section section = entry.getValue();
            // Ignore invisible section
            if (!section.isVisible())
                continue;
            int sectionTotal = section.getSectionItemsTotal();
            // Check if position is in this section
            if (position >= currentPosition && position <= (currentPosition + sectionTotal - 1)){
                if (section.hasHeader()){
                    if (position == currentPosition){
                        // Delegate the binding to the section header
                        getSectionForPosition(position).onBindHeaderViewHolder(holder);
                        return;
                    }
                }

                if (section.hasFooter()){
                    if (position == (currentPosition + sectionTotal - 1)){
                        // Delegate the binding to the section footer
                        getSectionForPosition(position).onBindFooterViewHolder(holder);
                        return;
                    }
                }

                // Delegate the binding to the section content
                getSectionForPosition(position).onBindContentViewHolder(holder, getSectionPosition(position));
                return;
            }
            currentPosition += sectionTotal;
        }
        throw new IndexOutOfBoundsException("Invalid position");
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Map.Entry<String, Section> entry : mSections.entrySet()) {
            Section section = entry.getValue();
            // Ignore invisible sections
            if (!section.isVisible())
                continue;
            count += section.getSectionItemsTotal();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int currentPosition = 0;
        for (Map.Entry<String, Section> entry : mSections.entrySet()) {
            Section section = entry.getValue();
            // Ignore invisible sections
            if (!section.isVisible())
                continue;
            int sectionTotal = section.getSectionItemsTotal();
            // check if position is in this section
            if (position >= currentPosition && position <= (currentPosition + sectionTotal - 1)) {
                Integer viewType = mSectionViewTypeNums.get(entry.getKey());
                if (section.hasHeader()) {
                    if (position == currentPosition)
                        return viewType;
                }

                if (section.hasFooter())
                    if (position == (currentPosition + sectionTotal - 1))
                        return viewType + 1;
                
                switch(section.getState()){
                    case LOADED:
                        return viewType + 2;
                    case LOADING:
                        return viewType + 3;
                    case FAILED:
                        return viewType + 4;
                    default :
                        throw new IllegalArgumentException("Invalid state");
                }
            }
            currentPosition += sectionTotal;
        }
        throw new IndexOutOfBoundsException("Invalid position");
    }

    /**
     * Returns the Section ViewType of an item based on the position in the adapter:
     * - SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER
     * - SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER
     * - SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED
     * - SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING
     * - SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED
     *
     * @param position position in the adapter
     * @return SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER, VIEW_TYPE_FOOTER,
     * VIEW_TYPE_ITEM_LOADED, VIEW_TYPE_LOADING or VIEW_TYPE_FAILED
     */
    public int getSectionItemViewType(int position){
        int viewType = getItemViewType(position);
        return viewType % VIEW_TYPE_QTY;
    }

    /**
     * Add a section to this RecyclerView with a random tag
     * @param section section to be added
     * @return generated tag
     */
    public String addSection(Section section){
        String tag = UUID.randomUUID().toString();
        addSection(tag, section);
        return tag;
    }

    /**
     * Add a section to this RecyclerView
     * @param tag Unique identifier of the section
     * @param section section to be added
     */
    public void addSection(String tag, Section section){
        this.mSections.put(tag, section);
        this.mSectionViewTypeNums.put(tag, mViewTypeCount);
        mViewTypeCount += VIEW_TYPE_QTY;
    }

    /**
     * Return the section with the tag provided
     *
     * @param tag unique identifier of the section
     * @return section
     */
    public Section getSection(String tag) {

        return this.mSections.get(tag);
    }

    /**
     * Remove section from this recyclerview.
     *
     * @param tag unique identifier of the section
     */
    public void removeSection(String tag) {
        this.mSections.remove(tag);
    }

    /** Remove all sections from this recyclerview. */
    public void removeAllSections() {
        this.mSections.clear();
    }

    /**
     * Return a map with all sections of this adapter
     * @return a map with all sections
     */
    public LinkedHashMap<String, Section> getSectionsMap() {
        return mSections;
    }


    /**
     * Returns the Section object for a position in the adapter
     *
     * @param position position in the adapter
     * @return Section object for that position
     */
    private Section getSectionForPosition(int position) {
        int currentPosition = 0;
        for (Map.Entry<String, Section> entry : mSections.entrySet()) {
            Section section = entry.getValue();
            // Ignore invisible sections
            if (!section.isVisible())
                continue;
            int sectionTotal = section.getSectionItemsTotal();
            // Check if position is in this section
            if (position >= currentPosition && position <= (currentPosition + sectionTotal - 1))
                return section;
            currentPosition += sectionTotal;
        }
        throw new IndexOutOfBoundsException("Invalid position");
    }

    /**
     * Return the item position relative to the section.
     *
     * @param position position of the item in the adapter
     * @return position of the item in the section
     */
    private int getSectionPosition(int position) {
        int currentPosition = 0;
        for (Map.Entry<String, Section> entry : mSections.entrySet()) {
            Section section = entry.getValue();
            // Ignore invisible sections
            if (!section.isVisible())
                continue;
            int sectionTotal = section.getSectionItemsTotal();
            // Check if position is in this section
            if (position >= currentPosition && position <= (currentPosition + sectionTotal - 1))
                return position - currentPosition - (section.hasHeader() ? 1 : 0);
            currentPosition += sectionTotal;
        }
        throw new IndexOutOfBoundsException("Invalid position");
    }

    /**
     * A concrete class of an empty ViewHolder.
     * Should be used to avoid the boilerplate of creating a ViewHolder class for simple case
     * scenarios.
     */
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {

            super(itemView);
        }
    }
}
