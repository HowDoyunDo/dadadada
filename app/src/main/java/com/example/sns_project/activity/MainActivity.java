package com.example.sns_project.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sns_project.fragment.ChatsFragment;
import com.example.sns_project.fragment.HomeFragment;
import com.example.sns_project.fragment.ProfileFragment;
import com.example.sns_project.R;
import com.example.sns_project.fragment.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.example.sns_project.Util.showToast;


/*
TODO 1. 채팅 프로필 이미지 변경시 storage 저장 통합 (o)
     2. 툴바 만들기
     3. 게시판 검색기능, 사용자 이름 나오게 수정
     4. 관광 api 통합하기 + 구글 map API 등록하기
     5. 사용자 정보 구체화 DB저장
 */

public class MainActivity extends BasicActivity {
    private long backPressedTime; // 두번눌러서 앱종료
    private Toast backToast; // Toast value

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); //화면 전환 금지 설정
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //사용자가 로그인 되어있는지 확인

        FirebaseStorage storage = FirebaseStorage.getInstance(); //Firebase Storage 초기화
        storageRef = storage.getReference();

        if (firebaseUser == null) { //사용자가 로그인 되지 않았다면
            myStartActivity(LoginActivity.class); //LoginActivity로 이동
        } else { //사용자가 로그인을 한 상태라면
            firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화(DataBase)
            DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid()); //firebase DB users 경로에서 uid가 있는지 확인(회원 정보가 등록되어있는지 확인)
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) { //문서의 데이터를 가져왔을 경우
                            showToast(MainActivity.this, "회원님 반갑습니다!");
                        } else { //문서의 데이터를 가져오지 못했을 경우
                            showToast(MainActivity.this, "회원 정보를 입력해 주세요.");
                            myStartActivity(MemberInitActivity.class); //회원등록 초기설정 페이지로 이동
                        }
                    }
                }
            });
        }


        // 여기부터 bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                new HomeFragment()).commit();

    }

    // Action Bar 에서 서치(돋보기 아이콘, Search), 게시판(Bulletin Board) 기능 부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu); // menu_option 을 선언해놓음
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search: // 돋보기 옵션을 눌렀을 경우
                myStartActivity(SearchActivity.class);
                return true;

            case R.id.menu_bulletin_board: // 게시판 옵션을 눌렀을 경우
                myStartActivity(GuideBoardActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item); //
        }
    }

    // bottom navigation 에서 해당하는 fragment 호출 함수
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.nav_chat_users:
                        selectedFragment = new UsersFragment();
                        break;

                    case R.id.nav_chat:
                        selectedFragment = new ChatsFragment();
                        break;

                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                        selectedFragment).commit();

                return true;
            };

    // 두 번 눌러서 창을 나가기 위해 시간 설정 함수
    @Override
    public void onBackPressed(){

        if (backPressedTime + 2000 > System.currentTimeMillis()){ // 일정시간 이내에 두번 클릭인지 측정
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void myStartActivity(Class c) { //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}