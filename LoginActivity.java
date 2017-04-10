package bobkallepalle.wya;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference wyaDatabaseRoot = FirebaseDatabase.getInstance().getReference().getRoot();

    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSigninButton;
    private TextView mSignUp;
    Map<String, Object> wyaUserList = new HashMap<String, Object>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (TextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSigninButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignUp = (TextView ) findViewById(R.id.textSignUp);
        mSigninButton.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        fbAuth = FirebaseAuth.getInstance();

        if(fbAuth.getCurrentUser() != null)
        {
            wyaUserList.put(
                    fbAuth.getCurrentUser().getUid(), fbAuth.getCurrentUser().getDisplayName()
            );
            wyaDatabaseRoot.updateChildren(wyaUserList);
            wyaDatabaseRoot.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator I = dataSnapshot.getChildren().iterator();
                    Set<String> set = new HashSet<String>();
                    while (I.hasNext()){
                        set.add(((DataSnapshot)I.next()).getKey());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            finish();
            Intent mapsIntent = new Intent(getBaseContext(), MapsActivity.class);
            mapsIntent.putExtra("wyaID", fbAuth.getCurrentUser().getUid());
            startActivity(mapsIntent);
        }


    }

    @Override
    public void onClick(View v) {
       if(v == mSigninButton)
       {
           SignIn();

       }
        else if(v == mSignUp)
        {
            register();
        }


    }

    private void SignIn() {
        if((mEmailView.getText().toString().trim() == null))
        {
            Toast.makeText(this, "Enter email ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if((mPasswordView.getText().toString().trim() == null))
        {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        fbAuth.signInWithEmailAndPassword(mEmailView.getText().toString().trim(),mPasswordView.getText().toString().trim( )).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    wyaUserList.put(
                            fbAuth.getCurrentUser().getUid(), fbAuth.getCurrentUser().getDisplayName()
                    );
                    wyaDatabaseRoot.updateChildren(wyaUserList);
                    wyaDatabaseRoot.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator I = dataSnapshot.getChildren().iterator();
                            Set<String> set = new HashSet<String>();
                            while (I.hasNext()){
                                set.add(((DataSnapshot)I.next()).getKey());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
                    startActivity(new Intent(getBaseContext(), MapsActivity.class));
                }
            }
        });

    }

    private void register() {
        if((mEmailView.getText().toString().trim() == null))
        {
            Toast.makeText(this, "Enter email ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if((mPasswordView.getText().toString().trim() == null))
        {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        fbAuth.createUserWithEmailAndPassword(mEmailView.getText().toString().trim(), mPasswordView.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getBaseContext(), "User Registed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fbAuth !=null)
            fbAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(mAuthListener);
    }
}