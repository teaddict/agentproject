# agentproject
proje iş dersi için geliştirilen android uygulama.


Projenin Özeti
Projemiz, birbirleriyle görüşmek zorunda olan iki ajanın serüvenini ve bazı ince
detayları ele alıyor. Kısaca hikayelemek istersek eğer, ajanlarımıza A ve B diyelim. A ve
B birbirlerini tanımıyorlar, üstlerinden kendilerine görüşcekleri kişinin bi resmi iletiliyor
ve resmin arkasında ise buluşma koordinatları, ayrıca mesajlaşma programına giriş için
gerekli id ve şifre bulunuyor. Ayrıca veritabanın her görüşme için özel hotspot name ve
pass oluşturuluyor. Bu programın çalışması için ise belli şartlar var, A ve B birbirlerine
en fazla 50 metre uzak olabilirler, birbirlerini tanımadıkları için ellerindeki resme göre
birbirlerinin kimliklerini doğrulayacaklar, 50 metre şartı buyüzden konuldu. İki ajan da
50 metre kuralını yerine getirdikten sonra programda buton yeşile dönüyor ve tıklanacak
hale geliyor, butona tıklanıldığında ise ilk önce veri tabanından otomatik oluşturulmuş
hotspot name çekiyor, ve wireless ile tarıyor etrafı, eğer öyle bi hotspot varsa elindeki
şifreyle bu ağa bağlanıyor. Eğer yoksa kendi server durumuna geçiyor ve elinde hotspot
name ve pass ile gizli bi ağ oluşturuyor. Diğer ajanda bu gizli ağa bağlandıktan sonra
artık mesajlaşma servisine geçiş sağlanıyor. Bu servis online olarak çalışıyor ve program
kapatıldıktan sonra telefondan tüm bilgiler siliniyor.

Project Summary
It is about two agents and their adventures. If we want to tell the story briefly, we should call our agents A and B. 
A and B dont know each other. Their seniors give them the picture of person with whom they should meet. 
On the back of the picture is written the meeting coordinates and besides the id and password for instant messaging application.
The database generates private hotspot name and pass for every meeting. The program has some rules before it works;
A and B have to be close to fifty meters from each other because they dont know each other they verify each other with 
pictures. The fifty meter rule is for verifying each other. After the distance under fifty meter the button turns green
so you can click it. After clicking it the program gets the hotspot name from database and scan with wifi. If there is not 
any hotspot with this name, it creates one, if there is one , it connects to hotspot with password. After they connected
they can share information, it is crypted too. After closing the application everything is deleted on phone but we keep
the details in our database.
