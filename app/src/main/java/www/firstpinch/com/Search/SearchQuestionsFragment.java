package www.firstpinch.com.firstpinch2.Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseMemberRecyclerObject;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 03-10-2016.
 */

public class SearchQuestionsFragment extends Fragment {

    public String GET_SEARCHED_QUESTION_URL = "http://54.169.84.123/api/questionSearch";
    private RecyclerView recyclerView;
    private SearchQuestionsFragmentAdapter recyclerAdapter;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    String query;
    List<SearchQuestionsFragmentObject> question_data;
    LinearLayout ll_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        question_data = ((SearchableActivity) getActivity()).getQuestion_data();
        query = ((SearchableActivity) getActivity()).getQuery();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.member_fragment_recyclerview, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                question_data.clear();
                page_index = 1;
                get_searched_question(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        recyclerView = (RecyclerView) view.findViewById(R.id.memberfragment_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        recyclerAdapter = new SearchQuestionsFragmentAdapter(getActivity(), question_data);
        if(question_data.isEmpty()){
            swipeRefreshLayout.setVisibility(View.GONE);
            ll_empty_recyclerview.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            ll_empty_recyclerview.setVisibility(View.GONE);
        }recyclerView.setAdapter(recyclerAdapter);
        final MyLinearLayoutManager llm = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 3 && !loading) {
                    //bottom of list!
                    //Log.e("position", "" + llm.findLastCompletelyVisibleItemPosition());
                    Log.e("position", "" + llm.findLastVisibleItemPosition());
                    page_index++;
                    get_searched_question(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return view;
    }

    private void get_searched_question(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SEARCHED_QUESTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray feed3 = obj.getJSONArray("questions");
                            for (int i = 0; i < feed3.length(); i++) {
                                SearchQuestionsFragmentObject current = new SearchQuestionsFragmentObject();
                                JSONObject feedBlock3 = feed3.getJSONObject(i);
                                current.setId(feedBlock3.getString("id"));
                                current.setQuestionTitle(feedBlock3.getString("title"));
                                current.setProfileImageUrl(feedBlock3.getString("user_image"));
                                question_data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Page-Index", String.valueOf(page_index));
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Search-Query", query);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*if (page_index == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }*/

    }

    public static List<SearchQuestionsFragmentObject> getData() {
        List<SearchQuestionsFragmentObject> data = new ArrayList<SearchQuestionsFragmentObject>();
        String questionTitle[] = {"What is the missing number?", "What is the importance of ' Diksha' ?",
                "What is the significance of fasting?", "What gets wetter the more it dries?", "What is the difference between japa and meditation?",
                "What has an eye but can not see?", "What are the health benefits of green tea and white tea?",
                "What colour carpet should I buy? I have cream colour sofas.",
                "What do you get if you put a radio in the fridge?", "What do you get if you cross a rooster and a dog?",
                "What gets wetter the more it dries?", "What is the difference between japa and meditation?",
                "What has an eye but can not see?", "What are the health benefits of green tea and white tea?",
                "What colour carpet should I buy? I have cream colour sofas.",
                "What do you get if you put a radio in the fridge sdifuh sawoi eovnxz aslkm alwmc lwoepkgru sj s?"};

        String profileImageUrlList[] = {"https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg",
                "https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};

        for (int i = 0; i < questionTitle.length; i++) {
            SearchQuestionsFragmentObject current = new SearchQuestionsFragmentObject();
            current.setQuestionTitle(questionTitle[i]);
            current.setProfileImageUrl(profileImageUrlList[i]);

            data.add(current);
        }
        return data;
    }

}