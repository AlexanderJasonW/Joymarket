module mod {
	opens main;
	opens model;
	opens util;
	opens view;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.controls;
}