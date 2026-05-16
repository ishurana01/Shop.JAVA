package service;

import model.CartItem;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class EmailService {

    private static final String ADMIN_EMAIL  = "ranaishu620@gmail.com"; // ← your Gmail
    private static final String APP_PASSWORD = "pqlvnuofuvyaaxph";     // ← 16 char app password

    public void sendInvoiceEmail(int orderId, String customerName,
                                 String customerEmail, List<CartItem> items,
                                 String deliveryAddress) {

        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        double gst      = subtotal * 0.18;
        double total    = subtotal + gst;

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.ssl.trust",       "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols",   "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ADMIN_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ADMIN_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(ADMIN_EMAIL)
            );
            message.setSubject("New Order #" + orderId + " - SHOP.JAVA");
            message.setContent(
                    buildEmailBody(orderId, customerName, customerEmail,
                            items, subtotal, gst, total, deliveryAddress),
                    "text/html; charset=utf-8"
            );

            Transport.send(message);
            System.out.println("Invoice email sent successfully!");

        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEmailBody(int orderId, String customerName,
                                  String customerEmail, List<CartItem> items,
                                  double subtotal, double gst, double total, String deliveryAddress) {
        StringBuilder rows = new StringBuilder();
        for (CartItem item : items) {
            rows.append(
                    "<tr>" +
                            "<td style='padding:12px;border-bottom:1px solid #2a2a3d'>"
                            + item.getProduct().getName() + "</td>" +
                            "<td style='padding:12px;border-bottom:1px solid #2a2a3d;" +
                            "text-align:center'>" + item.getQuantity() + "</td>" +
                            "<td style='padding:12px;border-bottom:1px solid #2a2a3d;" +
                            "text-align:right'>Rs." +
                            String.format("%,.2f", item.getSubtotal()) + "</td>" +
                            "</tr>"
            );
        }

        return
                "<div style='font-family:Arial,sans-serif;max-width:620px;" +
                        "margin:auto;background:#0a0a0f;color:#e8e8f0;padding:36px;" +
                        "border-radius:16px;border:1px solid #2a2a3d'>" +

                        "<h1 style='text-align:center;color:#6c63ff'>SHOP.JAVA</h1>" +
                        "<h2 style='text-align:center;color:#43e97b'>New Order Received!</h2>" +
                        "<hr style='border-color:#2a2a3d;margin:20px 0'/>" +

                        "<p><b style='color:#6b6b8a'>Order ID: </b>#" + orderId + "</p>" +
                        "<p><b style='color:#6b6b8a'>Customer: </b>" + customerName + "</p>" +
                        "<p><b style='color:#6b6b8a'>Email: </b>"    + customerEmail + "</p>" +
                        "<p><b style='color:#6b6b8a'>Date: </b>" +
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "</p>" +
                        "<div style='background:#1a1a26;border-radius:12px;padding:16px;margin-bottom:20px'>" +
                        "<p style='color:#6b6b8a;font-size:0.78rem;text-transform:uppercase;" +
                        "letter-spacing:0.5px;margin-bottom:8px'>Delivery Address</p>" +
                        "<p style='font-size:0.9rem;line-height:1.7;color:#b0b0c8'>" +
                        deliveryAddress.replace("\n", "<br/>") + "</p>" +
                        "</div>" +

                        "<table style='width:100%;border-collapse:collapse;margin:20px 0'>" +
                        "<thead><tr style='background:#1a1a26'>" +
                        "<th style='padding:12px;text-align:left;color:#6b6b8a'>Product</th>" +
                        "<th style='padding:12px;color:#6b6b8a'>Qty</th>" +
                        "<th style='padding:12px;text-align:right;color:#6b6b8a'>Amount</th>" +
                        "</tr></thead>" +
                        "<tbody>" + rows + "</tbody></table>" +

                        "<div style='background:#1a1a26;border-radius:12px;padding:20px'>" +
                        "<div style='display:flex;justify-content:space-between;" +
                        "color:#6b6b8a;padding:6px 0'>" +
                        "<span>Subtotal</span><span>Rs." +
                        String.format("%,.2f", subtotal) + "</span></div>" +
                        "<div style='display:flex;justify-content:space-between;" +
                        "color:#6b6b8a;padding:6px 0'>" +
                        "<span>GST 18%</span><span>Rs." +
                        String.format("%,.2f", gst) + "</span></div>" +
                        "<hr style='border-color:#2a2a3d;margin:10px 0'/>" +
                        "<div style='display:flex;justify-content:space-between;" +
                        "font-weight:bold;color:#43e97b;padding:6px 0'>" +
                        "<span>Grand Total</span><span>Rs." +
                        String.format("%,.2f", total) + "</span></div>" +
                        "</div>" +

                        "<p style='text-align:center;margin-top:24px;color:#6b6b8a;" +
                        "font-size:0.85rem'>Automated notification from SHOP.JAVA</p>" +
                        "</div>";
    }
}