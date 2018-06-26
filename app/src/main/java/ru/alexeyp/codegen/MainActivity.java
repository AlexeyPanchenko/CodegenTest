package ru.alexeyp.codegen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.alexeyp.codegen_annotations.TestAnnotation;

public class MainActivity extends AppCompatActivity {

	@TestAnnotation
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
