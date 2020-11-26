package com.transporteruser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
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
import com.transporteruser.api.UserService;
import com.transporteruser.bean.User;
import com.transporteruser.databinding.CreateProfileActivityBinding;


import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateProfileActivity extends AppCompatActivity {
    CreateProfileActivityBinding binding;
    String currentUserId;
    Uri imageUri;
    SharedPreferences sp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = FirebaseAuth.getInstance().getUid();
        sp = getSharedPreferences("user", MODE_PRIVATE);
        binding = CreateProfileActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        }

        binding.btnEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in, 1);
            }
        });


            binding.btncreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageUri != null) {
                        String name = binding.etusername.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            binding.etusername.setError("Username required");
                            return;
                        }
                        String phoneNumber = binding.etphonenumber.getText().toString();
                        if (phoneNumber.length() <= 9 ) {
                            binding.etphonenumber.setError("Phone number is required");
                            return;
                        }
                        String streetAddress = binding.etStreetAddress.getText().toString();
                        if (TextUtils.isEmpty(streetAddress)) {
                            binding.etStreetAddress.setError("street address is required ");
                            return;
                        }

                        String cityAddress = binding.etCityAddress.getText().toString();
                        if (TextUtils.isEmpty(cityAddress)) {
                            binding.etCityAddress.setError("city address is required ");
                            return;
                        }

                        String stateAddress = binding.etStateAddress.getText().toString();
                        if (TextUtils.isEmpty(stateAddress)) {
                            binding.etStateAddress.setError("state address is required ");
                            return;
                        }
                        String address = streetAddress + "," + cityAddress + "," + stateAddress;
                        String token = FirebaseInstanceId.getInstance().getToken();

                        File file = FileUtils.getFile(CreateProfileActivity.this, imageUri);
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(imageUri)),
                                        file
                                );

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                        RequestBody userName = RequestBody.create(okhttp3.MultipartBody.FORM, name);

                        RequestBody userId = RequestBody.create(okhttp3.MultipartBody.FORM, currentUserId);

                        RequestBody userAddress = RequestBody.create(okhttp3.MultipartBody.FORM, address);

                        RequestBody userContact = RequestBody.create(okhttp3.MultipartBody.FORM, phoneNumber);

                        RequestBody userToken = RequestBody.create(okhttp3.MultipartBody.FORM, token);

                        UserService.UserApi userApi = UserService.getTransporterApiIntance();
                        Call<User> call = userApi.saveProfile(body, userId, userName, userAddress, userContact, userToken);
                        final ProgressDialog pd = new ProgressDialog(CreateProfileActivity.this);
                        pd.setMessage("please wait while creating profile");
                        pd.show();
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    User user = response.body();
                                    saveDataLocally(user);
                                    Toast.makeText(CreateProfileActivity.this, "Create profile Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }else if(response.code() == 500){
                                    Log.e("Server","====>"+response.errorBody());
                                    Toast.makeText(CreateProfileActivity.this, "Server not respond", Toast.LENGTH_SHORT).show();
                                }
                            }


                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.e("Error ===", "===>" + t.getMessage());
                                Toast.makeText(CreateProfileActivity.this, "Failed : " + t, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(CreateProfileActivity.this, "please select image", Toast.LENGTH_SHORT).show();
                    }
                }

            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Delete");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            binding.civ.setImageURI(imageUri);
        }
    }

    private void saveDataLocally(User user) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", user.getName());
        editor.putString("userId", user.getUserId());
        editor.putString("imageUrl", user.getImageUrl());
        editor.putString("address", user.getAddress());
        editor.putString("contactNo", user.getContactNumber());
        editor.putString("token", user.getToken());
        editor.commit();
    }
}