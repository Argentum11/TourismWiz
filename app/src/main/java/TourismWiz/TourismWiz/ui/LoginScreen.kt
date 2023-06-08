package TourismWiz.TourismWiz.ui

import TourismWiz.TourismWiz.R
import TourismWiz.TourismWiz.data.FireBase
import TourismWiz.TourismWiz.data.FireBase.Companion.gson
import TourismWiz.TourismWiz.data.MyUser.Companion.user
import TourismWiz.TourismWiz.model.Restaurant
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalFocusManager
import com.google.firebase.firestore.DocumentSnapshot
//callback: (result: MutableList<Restaurant>) -> Unit
@Composable
fun LoginScreen(field:String, myItem:Any?,saveList : (MutableList<Restaurant>) -> Unit) {
    var isLoginDialogVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Column() {
        IconButton(
                onClick = {
                    if (user!=null) {
                        if(myItem==null) {
                            if(field=="Restaurant") {
                                var ml = mutableListOf<Restaurant>()
                                user?.email?.let {
                                    FireBase.getFavorite(it, field) {
                                        for (i in it) {
                                            val r: Restaurant = gson.fromJson(
                                                i.get("item").toString(),
                                                Restaurant::class.java
                                            )
                                            //Log.d("FireBaseRelated",i.toString())
                                            ml.add(r)
                                        }
                                        //Log.d("FireBaseRelated",ml.toString())
                                        saveList(ml)
                                    }
                                }
                            }
                        }else{
                            if(field=="Restaurant")
                                user?.email?.let { FireBase.addFavorite<Restaurant>(it,field,myItem as Restaurant) }
                        }
                    }
                    else {
                        isLoginDialogVisible = true
                    }
            }) {
            Image(
                painter = painterResource(R.drawable.heart),
                contentDescription = "Heart Icon",
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
            )
        }
    }
    if(isLoginDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {
            AlertDialog(
                onDismissRequest = {
                    isLoginDialogVisible = false
                    focusManager.clearFocus()
                },
                buttons = {
                    Column() {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.6f),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wiz),
                                contentDescription = "wiz",
                                modifier = Modifier.fillMaxWidth()

                            )
                            Text(
                                text = "Welcome",
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(text = "Email") }
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    if (email != "" && password != "") {
                                        FireBase.loginAccount(email, password, context)?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                user = task.result
                                                if(user!=null)
                                                    isLoginDialogVisible = false

                                            } else {
                                                user = null
                                            }
                                        }
                                    } else
                                        Toast.makeText(
                                            context,
                                            "field must have value",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Login")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    if (email != "" && password != "") {
                                        FireBase.createAccount(email, password, context)?.addOnCompleteListener { task ->
                                            user=task.result
                                            if(user!=null)
                                                isLoginDialogVisible = false
                                        }
                                    }
                                    else
                                        Toast.makeText(
                                            context,
                                            "field must have value",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Register")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            )
        }
    }
}