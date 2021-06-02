package org.lqs1848.cv.test;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;


public class App {
	public static void main(String[] args) {

		// 1 读取OpenCV自带的人脸识别特征XML文件
		// OpenCV 图像识别库一般位于 opencv\sources\data 下面
		CascadeClassifier facebook = new CascadeClassifier(
				"D:\\Software\\opencv-4.5.2\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
		// 2 读取测试图片
		Mat image = opencv_imgcodecs.imread("C:\\Users\\codec\\Downloads\\3.jpg");
		// 3 特征匹配
		RectVector face = new RectVector();
		facebook.detectMultiScale(image, face);
		// 4 匹配 Rect 矩阵 数组
		Rect[] rects = face.get();
		System.out.println("匹配到 " + rects.length + " 个人脸");
		// 5 为每张识别到的人脸画一个圈
		for (int i = 0; i < rects.length; i++) {
			opencv_imgproc.rectangle(image, new Point(rects[i].x(), rects[i].y()),
					new Point(rects[i].x() + rects[i].width(), rects[i].y() + rects[i].height()),
					new Scalar(0, 0, 255, 0));
			opencv_imgproc.putText(image, "Human", new Point(rects[i].x(), rects[i].y()),
					opencv_imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0, 0), 1,
					opencv_imgproc.LINE_AA, false);
		}
		
		// 6 展示图片
		// HighGui.imshow("人脸识别", image);
		opencv_imgcodecs.imwrite("C:\\Users\\codec\\Downloads\\2.jpg", image);
		// HighGui.waitKey(0);

	}
}
