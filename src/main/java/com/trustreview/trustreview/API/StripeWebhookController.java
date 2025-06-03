//package com.trustreview.trustreview.API;
//
//import com.stripe.model.Event;
//import com.stripe.model.checkout.Session;
//import com.stripe.net.Webhook;
//import com.trustreview.trustreview.Service.StripeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/stripe")
//public class StripeWebhookController {
//
//    @Value("${stripe.webhook.secret}")
//    private String endpointSecret;
//
//    @Autowired
//    private StripeService stripeService;
//
//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
//                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
//        try {
//            System.out.println("📩 Nhận webhook payload: " + payload);
//
//            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
//            System.out.println("✅ Event type: " + event.getType());
//
//            if ("checkout.session.completed".equals(event.getType())) {
//                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
//
//                System.out.println("👤 client_reference_id: " + session.getClientReferenceId());
//                System.out.println("💰 amount_total: " + session.getAmountTotal());
//
//                Long partnerId = Long.valueOf(session.getClientReferenceId());
//                Double amount = session.getAmountTotal() / 100.0;
//
//                stripeService.handleSuccessfulPayment(partnerId, amount);
//            }
//
//            return ResponseEntity.ok("success");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
//        }
//    }
//}
