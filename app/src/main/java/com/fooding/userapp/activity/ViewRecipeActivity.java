package com.fooding.userapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.userapp.APIService;
import com.fooding.userapp.FoodingApplication;
import com.fooding.userapp.R;
import com.fooding.userapp.data.Filter;
import com.fooding.userapp.data.Food;
import com.fooding.userapp.data.model.Ingredient;
import com.fooding.userapp.data.model.Nutrient;
import com.fooding.userapp.data.model.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewRecipeActivity extends AppCompatActivity {
//    @BindView(R.id.sendout) Button sendoutbutton;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.otherRecipesTitle) TextView otherRecipesTitle;
    @BindView(R.id.otherRecipes) ListView viewOtherRecipe;
    @BindView(R.id.ingredients) ListView ingredientList;
    @BindView(R.id.filter) ImageButton filterBtn;
    @BindView(R.id.camera) ImageButton cameraBtn;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.recentlyViewed) ImageButton recentlyViewedBtn;

    public ArrayList<String> results;
    public ArrayAdapter adapterI;
    public ArrayList<String> resultsO;
    public ArrayAdapter adapterO;
    public String serialNumber;
    private int filtersize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);

        /*************************************************************************************************************/
        // font setting
        final FoodingApplication app = FoodingApplication.getInstance();
        SharedPreferences fontSP = app.getMyPref();

        final String pathT = fontSP.getString("titleFont", "none");
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        title.setTypeface(font);
        otherRecipesTitle.setTypeface(font);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(fontSP.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.ViewRecipeActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            // change text color
            title.setTextColor(Color.parseColor("#ffffff"));
            otherRecipesTitle.setTextColor(Color.parseColor("#ffffff"));

            // change buttons
            filterBtn.setImageResource(R.mipmap.filter_white);
            cameraBtn.setImageResource(R.mipmap.camera_white);
            settingBtn.setImageResource(R.mipmap.settings_white);
            recentlyViewedBtn.setImageResource(R.mipmap.list_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.divide);
            tmp.setBackgroundColor(Color.parseColor("#ececec"));

            // listview divider/separator
            /*viewOtherRecipe.setDivider(new ColorDrawable(0xF0ECECEC));
            viewOtherRecipe.setDividerHeight(1);
            ingredientList.setDivider(new ColorDrawable(0xF0ECECEC));
            ingredientList.setDividerHeight(1);*/
        }
        /*************************************************************************************************************/

        Retrofit retrofit;
        APIService apiService;

        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

//        final FoodingApplication app = FoodingApplication.getInstance();
        final Food food = new Food();
        final Map<String, String> ingredients = new LinkedHashMap<String, String>();
        final Map<String, String> resultsO_map = new LinkedHashMap<String, String>();

        // serialNumber를 CameraActivity로부터 전달받거나 food에 일련번호를 저장하는 변수 추가
        serialNumber = getIntent().getStringExtra("code");
        results = new ArrayList<String>();
        adapterI = new ArrayAdapter(this, android.R.layout.simple_list_item_1, results) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                final FoodingApplication app = FoodingApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                textView.setTypeface(font);

                final Integer fontSize = myPref.getInt("fontSize", 16);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));
                }

                if(position < filtersize){  // change text color of filtered ingredient
                    //view.setBackgroundColor(getResources().getColor(R.color.transparent_Red));
                    textView.setTextColor(getResources().getColor(R.color.Red));
                }

                return view;
            }
        };
        resultsO = new ArrayList<String>();
        adapterO = new ArrayAdapter(this, android.R.layout.simple_list_item_1, resultsO) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                final FoodingApplication app = FoodingApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                textView.setTypeface(font);

                final Integer fontSize = myPref.getInt("fontSize", 16);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));
                }

                return view;
            }
        };
        ingredientList.setAdapter(adapterI);
        viewOtherRecipe.setAdapter(adapterO);
        /*Food food = app.getCurrentFood();
        serialNumber = food.getSerialNumber();*/

        final Map<String,String> tempMap = new LinkedHashMap<String, String>();


        /////////////get id and name array list from preference//////////////////
        final SharedPreferences myPref = getSharedPreferences("Mypref", MODE_PRIVATE);
        ArrayList<String> idSet = new ArrayList<String>();
        final ArrayList<String> nameSet = new ArrayList<String>();
        if(myPref.getStringSet("userListkey", null) != null) {
            idSet.addAll(myPref.getStringSet("userListkey",null));
        }
        if(myPref.getStringSet("userList", null) != null) {
            nameSet.addAll(myPref.getStringSet("userList",null));
        }
        final Map<String, String> userfilterMap = new LinkedHashMap<String, String>();
        for(int i = 0; i< idSet.size();i++){
            userfilterMap.put(idSet.get(i),nameSet.get(i));
        }

        // 레시피 이름 서버로부터 받아오기
        Log.i("serialNumber", serialNumber);
        Call<Recipe> comment_title = apiService.getRecipeInfo(serialNumber);
        comment_title.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if(response.isSuccessful()) {
                    String recipeName = response.body().getName();
                    title.setText(recipeName);

                    SharedPreferences recentlyViewed = getSharedPreferences("recentlyViewed", MODE_PRIVATE);
                    ArrayList<String> recipe = new ArrayList<String>();
                    if(recentlyViewed.getStringSet("recipeList", null) != null)
                        recipe.addAll(recentlyViewed.getStringSet("recipeList", null));
                    Set<String> recipeSet = new HashSet<String>(recipe);

                    if(recipeSet.size() >= 10) {
                        Iterator<String> iterator = recipeSet.iterator();
                        iterator.next();
                        iterator.remove();
                    }
                    recipeSet.add(serialNumber+"@"+recipeName);

                    for(String str : recipeSet) {
                        Log.i("recipe", str);
                    }

                    SharedPreferences.Editor editor = recentlyViewed.edit();
                    editor.putStringSet("recipeList", recipeSet);
                    editor.apply();

                    Log.i("Get Recipe Info", recipeName);
                } else {
                    Log.i("Get Recipe Info", "Fail");
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Log.i("Get Recipe Info", "On Failure");
                t.printStackTrace();
            }
        });


        //server call for ingredient
        Call<List<Ingredient>> comment = apiService.getIngredient(serialNumber);
        comment.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                if(response.isSuccessful()) {
                    results.clear();
                    ingredients.clear();

                    for(int i = 0; i < response.body().size(); i++) {
                        String temp = response.body().get(i).getName();
                        Log.i("oname", temp);
                        results.add(temp);
                        ingredients.put(response.body().get(i).getId(), response.body().get(i).getName());
                    }

                    if(response.body().size()!=0) adapterI.notifyDataSetChanged();

                    //////////filtering&compare userfilterList with dbList////////////
                    Set<String> dbIdSet = ingredients.keySet(); //get id set on db
                    //Toast.makeText(getApplicationContext(),""+dbIdSet.toString(),Toast.LENGTH_SHORT).show();
                    Set<String> userIdSet = userfilterMap.keySet();
                    Set<String> resultSet = new HashSet<>(dbIdSet);
                    resultSet.retainAll(userIdSet);
                    ArrayList<String> otherList = new ArrayList<>(dbIdSet);
                    ArrayList<String> filteredList = new ArrayList<>(resultSet);

                    //int filteredCount = 0;
                    for(int i = 0; i< filteredList.size();i++){
                        tempMap.put(filteredList.get(i),ingredients.get(filteredList.get(i)));
                        //filteredCount = filteredCount + i;
                    }
                    filtersize = filteredList.size();
                    for(int i = 0; i< otherList.size();i++){
                        tempMap.put(otherList.get(i),ingredients.get(otherList.get(i)));
                    }
                    //Toast.makeText(getApplicationContext(),""+tempMap.toString(),Toast.LENGTH_SHORT).show();
                    results.clear();
                    results.addAll(tempMap.values());
                    ingredients.clear();
                    ingredients.putAll(tempMap);
                    //text.setTextColor(getResources().getColor(R.color.Red));

                    //View nn = ingredientList.getChildAt(1);
                    //Toast.makeText(getApplicationContext(),""+ingredientList..toString(),Toast.LENGTH_SHORT).show();
                    ///////////////////////////////////////////////////////////////////////
                    food.setIngredient(ingredients);
                    app.setCurrentFood(food);

                } else {
                    Log.i("Get Ingredient", "Fail");
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Log.i("Get Ingredient", "Fail");
                t.printStackTrace();
            }
        });
        //*******************************
        //server call for other recipes
        Call<List<Recipe>> comment1 = apiService.getRecipeEatable(idSet,serialNumber);
        if(!(idSet.isEmpty())){
            Log.i("idset", idSet.get(0));
        }
        comment1.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if(response.isSuccessful()) {
                    resultsO.clear();
                    resultsO_map.clear();

                    for(int i = 0; i < response.body().size(); i++) {
                        Recipe temp = response.body().get(i);
                        resultsO.add(temp.getName());
                        resultsO_map.put(temp.getName(),temp.getId());
                    }

                    viewOtherRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final String chosenName = resultsO.get(position);
                            //Toast.makeText(getApplicationContext(),chosenName,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ViewRecipeActivity.this,ViewRecipeActivity.class);
                            intent.putExtra("code", resultsO_map.get(chosenName));
                            startActivity(intent);
                            finish();
                        }
                    });
                    if(response.body().size()!=0) adapterO.notifyDataSetChanged();
                } else {
                    Log.i("Get Recipe", "Fail");
                }
            }
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.i("Get Recipe", "Fail");
                t.printStackTrace();
            }
        });

        //////////// get nutrient here ///////////////
        //we will recieve nutrient info as {담백질, 몇gram}, the last one {calorie, 몇}
        final ArrayList<String> NutrientName = new ArrayList<String>();
        final ArrayList<String> NutrientGram = new ArrayList<String>();
        String calorie;
        Call<List<Nutrient>> comment2 = apiService.getNutrient(title.getText().toString());
        comment2.enqueue(new Callback<List<Nutrient>>() {
            @Override
            public void onResponse(Call<List<Nutrient>> call, Response<List<Nutrient>> response) {
                if(response.isSuccessful()) {
                    resultsO.clear();

                    for(int i = 0; i < response.body().size(); i++) {
                        //add nutrient here
                        NutrientName.add(response.body().get(i).getId());
                        NutrientGram.add(response.body().get(i).getName());
                    }

                } else {
                    Log.i("Get Nutrient", "Fail");
                }
            }
            @Override
            public void onFailure(Call<List<Nutrient>> call, Throwable t) {
                Log.i("Get Nutrient", "Fail");
                t.printStackTrace();
            }
        });

        /////////////////////////////////////////////

        /*sendoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, SendOutQRActivity.class));
                finish();
            }
        });*/

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, PopUpFilter.class));
                finish();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, CameraActivity.class));
                finish();
            }
        });

        recentlyViewedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, recentlyViewedActivity.class));
                finish();
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, SettingsActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed(); // this can go before or after your stuff below
        // do your stuff when the back button is pressed
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // super.onBackPressed(); calls finish(); for you
    }
}