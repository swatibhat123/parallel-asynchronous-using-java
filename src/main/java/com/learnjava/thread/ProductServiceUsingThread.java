package com.learnjava.thread;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

public class ProductServiceUsingThread {
    private ProductInfoService productInfoService;
    private ReviewService reviewService;

    public ProductServiceUsingThread(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) throws InterruptedException {
        stopWatch.start();

        ProductInfoRunnable productInfoRunnable = new ProductInfoRunnable(productId);
        Thread productInfoThread = new Thread(productInfoRunnable);

        ProductReviewRunnable productReviewRunnable = new ProductReviewRunnable(productId);
        Thread productReviewThread = new Thread(productReviewRunnable);

        productInfoThread.start();
        productReviewThread.start();

        productInfoThread.join();
        productReviewThread.join();

        ProductInfo productInfo = productInfoRunnable.getProductInfo();
        Review review = productReviewRunnable.getReview();

        stopWatch.stop();
        log("Total Time Taken : " + stopWatch.getTime());
        return new Product(productId, productInfo, review);
    }

    public static void main(String[] args) throws InterruptedException {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingThread productService = new ProductServiceUsingThread(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);

    }

    private class ProductInfoRunnable implements Runnable {
        private ProductInfo productInfo;
        private String productId;

        public ProductInfoRunnable(String productId) {
            this.productId = productId;
        }

        public String getProductId() {
            return productId;
        }

        public ProductInfo getProductInfo() {
            return productInfo;
        }

        @Override
        public void run() {
            this.productInfo = productInfoService.retrieveProductInfo(productId);
        }
    }

    private class ProductReviewRunnable implements Runnable {
        private String productId;
        private Review review;

        public ProductReviewRunnable(String productId) {
            this.productId = productId;
        }

        public Review getReview() {
            return review;
        }

        @Override
        public void run() {
            this.review = reviewService.retrieveReviews(productId);
        }
    }
}
