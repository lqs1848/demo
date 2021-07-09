package org.lqs1848.cv.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

public class OpenCV_LableCatch {

	public static void main(String[] args) {
		//加载图片为灰度图
		Mat src = opencv_imgcodecs.imread("E:\\cv\\1.jpg", opencv_imgcodecs.IMREAD_GRAYSCALE);

		int x = src.cols(); // 列
		int y = src.rows(); // 行

		if (x != y) { // 非正方形的图 截取中心部分为正方形
			if (x < y) {
				int j = (y - x) / 2;
				src = src.apply(new Rect(0, j, x, x));
			} else {
				int j = (x - y) / 2;
				src = src.apply(new Rect(j, 0, y, y));
			}
		} // if

		Mat gray = new Mat();
		//把大小不确定的图片 转换为固定 3000 * 3000
		opencv_imgproc.resize(src, gray, new Size(3000, 3000));
		
		//opencv_imgproc.resize(src, src, new Size(1000, 1000));
		//opencv_imgcodecs.imwrite("E:\\cv\\zfx.jpg", src);

		Mat target = new Mat();
		// 二值化
		opencv_imgproc.threshold(gray, target, 0, 255, opencv_imgproc.THRESH_OTSU);
		opencv_imgcodecs.imwrite("E:\\cv\\ezh.jpg", target);

		// new Scalar(255) 初始matrix的数值 不定义每次腐蚀膨胀的结果都会不一样 坑爹
		Mat kernel = new Mat(new Size(20, 20), opencv_core.CV_8UC1, new Scalar(255));
		// 腐蚀膨胀
		opencv_imgproc.morphologyEx(target, src, opencv_imgproc.MORPH_OPEN, kernel, new Point(-1, -1), 3,
				opencv_imgproc.MORPH_RECT, opencv_imgproc.morphologyDefaultBorderValue());
		opencv_imgcodecs.imwrite("E:\\cv\\fspz.jpg", src);

		// 反色
		opencv_core.bitwise_not(src, src);
		opencv_imgcodecs.imwrite("E:\\cv\\fs.jpg", src);

		// 存放轮廓
		MatVector contours = new MatVector();
		Mat hierarchy = new Mat();
		// 查找轮廓
		opencv_imgproc.findContours(src, contours, hierarchy, opencv_imgproc.RETR_TREE,
				opencv_imgproc.CHAIN_APPROX_NONE);

		//Mat contours_img = new Mat(src.size(), opencv_core.CV_8U, new Scalar(255));
		List<Rect> rects = new ArrayList<>();
		IntIndexer hie = hierarchy.createIndexer();
		for (int i = 0; i < contours.size(); i++) {
			if (hie.get(0, i, 2) == -1) {
				Mat contour = contours.get(i);
				double area = opencv_imgproc.contourArea(contour);
				double mins = 100;
				if (area > mins) {
					Rect rect = opencv_imgproc.boundingRect(contour);
					rects.add(rect);
					//opencv_imgproc.drawContours(contours_img, contours, i, new Scalar(0), -1, opencv_imgproc.LINE_8, hierarchy, Integer.MAX_VALUE, new Point());
//					opencv_imgcodecs.imwrite("E:\\cv\\x_" + i + ".jpg", target.apply(rect));
				} // if
			}
		} // for
			// opencv_imgcodecs.imwrite("E:\\cv\\x_.jpg", contours_img);

		kernel = new Mat(new Size(7, 7), opencv_core.CV_8UC1, new Scalar(255));
		
		int s1_id = 0, s2_id = 0, s1_jump = 0, s2_jump = 0;
		for (int i = 0; i < rects.size(); i++) {
			Rect rect = rects.get(i);
			Mat roi = target.apply(rect);
			if (roi.rows() <= roi.cols())
				opencv_imgproc.resize(roi, roi, new Size(300, 100), 0, 0, opencv_imgproc.INTER_NEAREST);
			else
				opencv_imgproc.resize(roi, roi, new Size(100, 300), 0, 0, opencv_imgproc.INTER_NEAREST);
			
			opencv_imgproc.morphologyEx(roi, roi, opencv_imgproc.MORPH_GRADIENT, kernel, new Point(-1, -1), 1,
					opencv_imgproc.MORPH_RECT, opencv_imgproc.morphologyDefaultBorderValue());
			
			//opencv_imgproc.medianBlur(roi, roi, 7);
			
			int t_jump = stringJudge(roi);

			// 选出跳变次数最多的两个轮廓
			//if (t_jump < 20) {
				if (t_jump >= s1_jump) {
					s2_id = s1_id;
					s2_jump = s1_jump;
					s1_id = i;
					s1_jump = t_jump;
				} else if (t_jump < s1_jump && t_jump >= s2_jump) {
					s2_id = i;
					s2_jump = t_jump;
				}
			//} // if
		} // for

		// 结果排序，坐标最左为第一串数字
		if (rects.get(s1_id).width() < rects.get(s1_id).height()
				&& rects.get(s1_id).tl().y() < rects.get(s2_id).tl().y()) {
			s1_id = s1_id + s2_id;
			s2_id = s1_id - s2_id;
			s1_id = s1_id - s2_id;
		}

		//把识别到的轮廓稍微扩大一点点 防止文字在图片最边缘导致无法被识别
		Rect ns1 = rects.get(s1_id);
		if (isCentre(target, ns1))
			ns1 = new Rect(ns1.x() - 30, ns1.y() - 30, ns1.width() + 60, ns1.height() + 60);
		Rect ns2 = rects.get(s2_id);
		if (isCentre(target, ns2))
			ns2 = new Rect(ns2.x() - 30, ns2.y() - 30, ns2.width() + 60, ns2.height() + 60);

		Mat dst1 = gray.apply(ns1);
		Mat dst2 = gray.apply(ns2);

		if (dst1.cols() < dst1.rows()) {
			opencv_core.transpose(dst1, dst1);
			opencv_core.flip(dst1, dst1, 1);
		}
		if (dst2.cols() < dst2.rows()) {
			opencv_core.transpose(dst2, dst2);
			opencv_core.flip(dst2, dst2, 1);
		}
		
		OCR ocr = new OCR();
		opencv_imgproc.resize(dst1, dst1, new Size(550, 250), 0, 0, opencv_imgproc.INTER_NEAREST);
		String number = ocr.getNumber(mat2Byte(dst1));
		System.out.println("111:"+number);
		opencv_imgcodecs.imwrite("E:\\cv\\r_111.jpg", dst1);
		
		dst1 = OpenCVUtils.adjustAngle(dst1);
		opencv_imgcodecs.imwrite("E:\\cv\\r_111_jz.jpg", dst1);
		number = ocr.getNumber(mat2Byte(dst1));
		System.out.println("xz 111:"+number);
		
		opencv_imgproc.resize(dst2, dst2, new Size(550, 250), 0, 0, opencv_imgproc.INTER_NEAREST);
		number = ocr.getNumber(mat2Byte(dst2));
		System.out.println("222:"+number);
		opencv_imgcodecs.imwrite("E:\\cv\\r_222.jpg", dst2);
		
		dst2 = OpenCVUtils.adjustAngle(dst2);
		number = ocr.getNumber(mat2Byte(dst2));
		System.out.println("xz 222:"+number);
		opencv_imgcodecs.imwrite("E:\\cv\\r_222_jz.jpg", dst2);
	}
	
	public static byte[] mat2Byte(Mat matrix) {
		/*
		try {
			File file = File.createTempFile("ocr", ".jpg");
			opencv_imgcodecs.imwrite(file.getPath(), matrix);
			int length = (int) file.length();
	        byte[] data = new byte[length];
	        new FileInputStream(file).read(data);
	        return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		*/
		ByteBuffer mob = ByteBuffer.allocate(matrix.arrayWidth() * matrix.arrayHeight());
		opencv_imgcodecs.imencode(".png", matrix, mob);
		byte[] byteArray = mob.array();
		return byteArray;
        
	}


	// 判断轮廓 是否在中心范围
	public static boolean isCentre(Mat mat, Rect rect) {
		int x = Math.abs((mat.cols() / 2) - (mat.cols() - rect.x()));
		int y = Math.abs((mat.rows() / 2) - (mat.rows() - rect.y()));
		return x < mat.cols() / 3 && y < mat.rows() / 3;
	}//method isCentre

	// 判断区域是否为数字
	public static int stringJudge(Mat img) {
		UByteRawIndexer intIndexer = img.createIndexer();
		int rows = img.rows();
		int cols = img.cols();
		int jump = 0;
		// 数字横着放，优先遍历列
		if (rows < cols) {
			for (int row = 0; row < rows; row++) {
				boolean wb_flag = false;// 白色到黑色
				boolean bw_flag = false;
				int t_jump = 0;
				for (int col = 0; col < cols; col++) {
					if (col + 1 < cols) {
						int now_point = intIndexer.get(row, col);
						int next_point = intIndexer.get(row, col + 1);
						if (now_point == 255 && next_point == 0) {
							wb_flag = true;
						}
						if (now_point == 0 && next_point == 255 && wb_flag) {
							bw_flag = true;
						}
						if (wb_flag && bw_flag) {
							++t_jump;
							wb_flag = false;
							bw_flag = false;
						}
					}
					if (t_jump > jump)
						jump = t_jump;
				}
			}
		} else {
			for (int col = 0; col < cols; col++) {
				boolean wb_flag = false;
				boolean bw_flag = false;
				int t_jump = 0;
				for (int row = 0; row < rows; row++) {
					if (row + 1 < rows) {
						int now_point = intIndexer.get(row, col);
						int next_point = intIndexer.get(row + 1, col);
						if (now_point == 255 && next_point == 0) {
							wb_flag = true;
						}
						if (now_point == 0 && next_point == 255 && wb_flag) {
							bw_flag = true;
						}
						if (wb_flag && bw_flag) {
							++t_jump;
							wb_flag = false;
							bw_flag = false;
						}
					}
					if (t_jump > jump)
						jump = t_jump;
				}
			}
		}
		return jump;
	}//method 
	
}//class
