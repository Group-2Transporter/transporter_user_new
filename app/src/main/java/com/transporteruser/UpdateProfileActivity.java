package com.transporteruser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.transporteruser.adapter.HistoryAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.User;
import com.transporteruser.databinding.CreateProfileActivityBinding;
import com.transporteruser.databinding.UpdateProfileActivityBinding;
import com.transporteruser.fragement.BottomSheetFragment;
import com.transporteruser.fragement.HistoryFragement;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Path;


public class UpdateProfileActivity extends AppCompatActivity {
    UpdateProfileActivityBinding binding;
    String currentUserId;
    Uri imageUri;
    String [] separated;
    UserService.UserApi userApi;
    SharedPreferences sp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = FirebaseAuth.getInstance().getUid();
        userApi = UserService.getTransporterApiIntance();
        sp = getSharedPreferences("user", MODE_PRIVATE);
        binding = UpdateProfileActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getLocallyData();
        getChangeListner();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        }

        binding.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in, 1);
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.tvName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    binding.tvName.setError("Username required");
                }
                String streetAddress = binding.tvStreetAddress.getText().toString();
                if (TextUtils.isEmpty(streetAddress)) {
                    binding.tvStreetAddress.setError("street address is required ");
                }

                String cityAddress = binding.tvCityAddress.getText().toString();
                if (TextUtils.isEmpty(cityAddress)) {
                    binding.tvCityAddress.setError("city address is required ");
                }

                String stateAddress = binding.tvStateAddress.getText().toString();
                if (TextUtils.isEmpty(stateAddress)) {
                    binding.tvStateAddress.setError("state address is required ");
                }

                String address = streetAddress + "," + cityAddress + "," + stateAddress;
                String phoneNumber = binding.tvContactNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber))
                    binding.tvContactNumber.setError("Phone number is required");
                String token = FirebaseInstanceId.getInstance().getToken();
                User user = new User();
                user.setUserId(currentUserId);
                user.setName(name);
                user.setAddress(address);
                user.setContactNumber(phoneNumber);
                user.setToken(token);

                Call<User> call = userApi.updateUser(user);
                final ProgressDialog pd = new ProgressDialog(UpdateProfileActivity.this);
                pd.setMessage("please wait while updating ");
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 200) {
                            User user = response.body();
                            saveDataLocally(user);
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(UpdateProfileActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        binding.addLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetFragment bottom = new BottomSheetFragment(UpdateProfileActivity.this);
                bottom.show(getSupportFragmentManager(), "");
                bottom.setCancelable(false);
            }
        });

        binding.historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateProfileActivity.this,HistoryActivity.class);
                startActivity(i);
            }
        });

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add("Delete");
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        String title = item.getTitle().toString();
//        if (title.equalsIgnoreCase("delete")) {
//            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
//        }
//        return true;
//    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            binding.civ.setImageURI(imageUri);
            try {
                File file = FileUtils.getFile(UpdateProfileActivity.this, imageUri);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(imageUri)),
                                file
                        );

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                RequestBody userId = RequestBody.create(okhttp3.MultipartBody.FORM, currentUserId);

                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("please wait while uploading image");
                pd.show();
                Call<User> call = userApi.updateUserImage(body,userId);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            User user = response.body();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("imageUrl", user.getImageUrl()).clear().commit();
                            editor.putString("imageUrl", user.getImageUrl()).commit();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocallyData() {
        binding.btnUpdate.setText("Update");
        binding.tvName.setText(sp.getString("name", ""));
        binding.tvUserName.setText(sp.getString("name",""));
        binding.tvContactNumber.setText(sp.getString("contactNo", ""));
        String address = sp.getString("address", "");
        separated = address.split(",");
        binding.tvStreetAddress.setText(separated[0]);
        binding.tvCityAddress.setText(separated[1]);
        binding.tvStateAddress.setText(separated[2]);
        Picasso.get().load(sp.getString("imageUrl", "")).into(binding.civ);
    }

    private void getChangeListner() {
        binding.tvUserName.setText(sp.getString("name",""));
        binding.tvName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    binding.tvUserName.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.tvStreet.setText(separated[0]);
        binding.tvStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    binding.tvStreet.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.tvCity.setText(separated[1]);
        binding.tvCityAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    binding.tvCity.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.tvState.setText(separated[2]);
        binding.tvStateAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    binding.tvState.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void saveDataLocally(User user) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", user.getName()).clear().commit();
        editor.putString("name", user.getName()).commit();
        editor.putString("userId", user.getUserId()).clear().commit();
        editor.putString("userId", user.getUserId()).commit();
        editor.putString("address", user.getAddress()).clear().commit();
        editor.putString("address", user.getAddress()).commit();
        editor.putString("contactNo", user.getContactNumber()).clear().commit();
        editor.putString("contactNo", user.getContactNumber()).commit();
        editor.putString("token", user.getToken()).clear().commit();
        editor.putString("token", user.getToken()).commit();
    }
}