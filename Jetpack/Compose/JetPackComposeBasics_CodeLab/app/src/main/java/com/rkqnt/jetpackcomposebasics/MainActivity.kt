package com.rkqnt.jetpackcomposebasics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkqnt.jetpackcomposebasics.ui.theme.BasicsCodelabTheme
import com.rkqnt.jetpackcomposebasics.ui.theme.JetpackComposeBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeBasicsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp(){
    // MyApp 컴포서블은 이 변수를 구독한것. 따라서 온보딩 컴포서블 버튼 클릭시 값이 바뀌면, 그 즉시 UI를 갱신함(if문)
    //var shouldShowOnboarding by remember { mutableStateOf(true) }
    // remember는 액티비티 생명주기에 따라 상태값을 잃을 수 있음(ex) 회전) rememberSabeable은 상태를 잃지 않음
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    // 온보딩 페이지를 만드는 용도(액티비티 없이)
    if(shouldShowOnboarding){
        // 하위 컴포서블로부터 이벤트를 넘겨 받기위해, 람다를 인자로 넘겨주는것. (버튼 콜백으로 람다함수 실행될것)
        OnboardingScreen(onContinueClicked = {shouldShowOnboarding = false})
    }else{
        Greetings()
    }
}

@Composable
fun Greetings(names: List<String> =List(1000){"$it"}){
    // Lazycolumn 은 RecyclerView 와 기능은 동일하나 내부 구조는 다름
    // (재활용이 아닌 영구적. 근데 View보다 가벼워서 괜춘)
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items=names){ name->
            //Greeting(name = name)
            Greeting2(name = name)
        }
    }
}

@Composable
private fun Greeting(name: String) {
    // 상호작용에 의해 UI의 변경이 있을때, Recomposition을 수행하는데, remember 객체는 이전 상태를 기억하여
    // Recomposition 수행시 값이 초기화되어버리지 않도록 한다.
    // 즉, 이 Composable 함수는 expanded 라는 상태를 구독한 것.
    var expanded by remember { mutableStateOf(false) }   // expanded 를 구독하고, 초기값은 false

    // var extraPadding = if(expanded.value) 48.dp else 0.dp

    // animateDpAsState는 지속적으로 업데이트 될 수 있는 값(target value, dp)을 가지는 상태 객체 반환
    // 애니매이션이 끝날떄까지 지속적으로 값을 업데이트 (사용자 광클로 애니매이션 방향 바뀌는 것도 바로바로 반응)
    // Spring은 자연스러운 애니매이션을 구성하는 spec으로 시간을 파라미터로 받지않음. (tween, repeatable 등의 spec도 있다)
    val extraPadding by animateDpAsState(
        if(expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(     // Surface는 컨테이너 레이아웃의 느낌이 아니라, 모든 컴포넌트에 한번에 설정할 Configuration의 느낌?
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello, ")
                Text(text = name)
            }
            OutlinedButton(
                onClick = { expanded= !expanded }
            ) {
                Text(if(expanded) "Show less" else "Show more")
            }
        }
    }
}

@Composable
private fun Greeting2(name: String){
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ){
        CardContent(name)
    }
}

@Composable
private fun CardContent(name: String){
    var expanded by remember { mutableStateOf(false)}
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "Hello, ")
            Text(
                text = name,
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if(expanded){
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                            "padding theme elit, sed do bouncy. ").repeat(4)
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit){
    Surface {
        // Column 컴포서블 function은 LinearLayout(Vertical) 과 같음
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,       // 열 컴포넌트들이 중앙에 위치
            horizontalAlignment = Alignment.CenterHorizontally      // 수평기준 가운데 정렬
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked                 // 인자로 전달받은 람다를 여기에 할당
            ){
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    JetpackComposeBasicsTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}


@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, name = "Greetings", widthDp = 320)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}

