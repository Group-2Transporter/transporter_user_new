   package com.transporteruser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.transporteruser.databinding.ActivityMainBinding;
import com.transporteruser.fragement.HistoryFragement;
import com.transporteruser.fragement.HomeFragement;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActionBarDrawerToggle toggle;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.navDrawer.setItemIconTintList(null);
        binding.bottomNav.setItemIconTintList(null);
        getFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragement()).commit();
        drawerInitialize();
        toggle = new ActionBarDrawerToggle(this,binding.drawer,binding.toolbar,R.string.open,R.string.close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

    }
    private void getFragment(){
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new HomeFragement();
                        break;
                    case R.id.history:
                        fragment = new HistoryFragement();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
                return true;
            }
        });
    }

    private void drawerInitialize(){
        binding.navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.drawer.closeDrawer(GravityCompat.START);
                Fragment selected = null;
                int id=item.getItemId();
                if(id == R.id.User){
                    Toast.makeText(MainActivity.this,"User",Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.home){
                    selected = new HomeFragement();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected).commit();
                }
                else if (id == R.id.history) {
                    selected = new HistoryFragement();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,selected).commit();
                }
                else if(id == R.id.TermAndCondition){
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                    i.putExtra(Intent.EXTRA_TEXT, "https://transpoter-uer.flycricket.io/privacy.html");
                    startActivity(Intent.createChooser(i, "Share URL"));

                }
                else if(id == R.id.PrivacyPoalcy){
                    Intent i = new Intent(MainActivity.this, PrivacyPolicy.class);
                    startActivity(i);
                }
                else if(id == R.id.ContectUs){
                    Intent i = new Intent(MainActivity.this, ContactUs.class);
                    startActivity(i);

                }
                else if(id == R.id.AboutUs){
                    Intent i = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(i);


                }
                else if(id == R.id.Logout){
                    if(InternetUtility.isNetworkConnected(MainActivity.this)) {
                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendUserToLoginActivity();
                                    }
                                });
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null){
            sendUserToLoginActivity();
        }
    }
    private void sendUserToLoginActivity(){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}