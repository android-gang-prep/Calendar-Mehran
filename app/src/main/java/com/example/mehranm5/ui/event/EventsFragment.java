package com.example.mehranm5.ui.event;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranm5.MainActivity;
import com.example.mehranm5.R;
import com.example.mehranm5.databinding.EventsFragBinding;
import com.example.mehranm5.databinding.FragmentGalleryBinding;
import com.example.mehranm5.ui.month.EventModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class EventsFragment extends Fragment {

    private EventsFragBinding binding;
    EventsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = EventsFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    List<EventModel> eventModels;
    List<EventModel> eventModelsSearch;

    int pos = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventModels = new ArrayList<>();
        eventModelsSearch = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(eventModels);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        viewModel.getEvents().observe(getViewLifecycleOwner(), eventModels1 -> {
            eventModelsSearch = new ArrayList<>(eventModels1);
            eventModels.clear();
            eventModels.addAll(eventModels1);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < eventModels1.size(); i++) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(eventModels1.get(i).getDate());
                String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
                Calendar calendarNow = new GregorianCalendar();
                String currentday = calendarNow.get(Calendar.YEAR) + "" + calendarNow.get(Calendar.MONTH) + "" + calendarNow.get(Calendar.DAY_OF_MONTH);

                if (key.equals(currentday)) {
                    pos = i;
                    break;
                }
            }
            binding.recyclerView.scrollToPosition(pos);

        });


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (eventModels.size() ==0) {
                    binding.eventsHeader.setVisibility(View.INVISIBLE);
                    return;
                }
                binding.eventsHeader.setVisibility(View.VISIBLE);

                List<HeaderModel> headerModels = new ArrayList<>();
                Calendar m = new GregorianCalendar();
                m.setTimeInMillis(eventModels.get(linearLayoutManager.findLastCompletelyVisibleItemPosition()).getDate());

                try {
                    ((MainActivity) getActivity()).setTitle(m.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

                } catch (Exception e) {
                }
                for (int i = linearLayoutManager.findFirstVisibleItemPosition(); i < linearLayoutManager.findLastVisibleItemPosition() + 1; i++) {
                    EventAdapter.ViewHolder viewHolder = (EventAdapter.ViewHolder) binding.recyclerView.findViewHolderForAdapterPosition(i);

                    EventModel eventModel = eventModels.get(i);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(eventModel.getDate());
                    HeaderModel headerModel = new HeaderModel(String.valueOf(calendar.get(Calendar.YEAR)), calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()), viewHolder.itemView.getTop() + ((viewHolder.itemView.findViewById(R.id.back).getBottom() - viewHolder.itemView.findViewById(R.id.back).getTop()) / 2));

                    List<EventModel> models = new ArrayList<>(getEvents(linearLayoutManager.findFirstCompletelyVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition()));


                    if (headerModels.size() == 0) {
                        boolean exist = false;
                        for (int j = 0; j < models.size(); j++) {
                            if (models.get(j).getDay().equals(eventModel.getDay())) {
                                exist = true;
                                break;
                            }
                        }
                        if (exist)
                            headerModel.setY((viewHolder.itemView.findViewById(R.id.back).getBottom() - viewHolder.itemView.findViewById(R.id.back).getTop()) / 2);
                    }
                    if (!contains(headerModels, headerModel)) {
                        headerModels.add(headerModel);
                    }
                }
                binding.eventsHeader.addData(headerModels);


                super.onScrolled(recyclerView, dx, dy);
            }
        });
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

                    menuInflater.inflate(R.menu.search_menu, menu);
                    MenuItem menuItem = menu.findItem(R.id.action_search);
                    SearchView searchView = (SearchView) menuItem.getActionView();
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String search) {


                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String search) {
                            eventModels.clear();
                            if (search.trim().isEmpty())
                                eventModels.addAll(eventModelsSearch);
                            else
                                for (int i = 0; i < eventModelsSearch.size(); i++) {
                                    if (eventModelsSearch.get(i).getTitle().toLowerCase().contains(search.trim().toLowerCase()))
                                        eventModels.add(eventModelsSearch.get(i));
                                }

                            adapter.notifyDataSetChanged();
                            if (eventModels.size() > 0)
                                binding.recyclerView.smoothScrollToPosition(0);
                            return false;
                        }
                    });
                if (getArguments().getBoolean("search", false)) {
                    menu.performIdentifierAction(R.id.action_search,0);
                }

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_today) {
                    for (int i = 0; i < eventModels.size(); i++) {
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(eventModels.get(i).getDate());
                        String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
                        Calendar calendarNow = new GregorianCalendar();
                        String currentday = calendarNow.get(Calendar.YEAR) + "" + calendarNow.get(Calendar.MONTH) + "" + calendarNow.get(Calendar.DAY_OF_MONTH);

                        if (key.equals(currentday)) {
                            pos = i;
                            break;
                        }
                    }
                    binding.recyclerView.smoothScrollToPosition(pos);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private List<EventModel> getEvents(int from, int to) {
        List<EventModel> models = new ArrayList<>();

        for (int i = from; i < to + 1; i++) {
            EventModel eventModel = eventModels.get(i);
            models.add(eventModel);
        }
        return models;

    }

    private boolean contains(List<HeaderModel> headerModels, HeaderModel headerModel) {

        for (int i = 0; i < headerModels.size(); i++) {
            if ((headerModels.get(i).getMonth() + headerModels.get(i).getDay()).equals(headerModel.getMonth() + headerModel.getDay()))
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}