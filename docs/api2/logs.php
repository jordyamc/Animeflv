<?php 
$target_path1 = "../logs/";
$randno=mt_rand(1,15000);

/* Add the original filename to our target path.
Result is "uploads/filename.extension" */


$target_path2 = $target_path1 . basename($randno.$_FILES['log']['name']);
if(move_uploaded_file($_FILES['log']['tmp_name'], $target_path2)) {
$to = 'jordyamc@hotmail.com'; 
//define the subject of the email 
$subject = str_replace(","," ",$_GET['info']);
//create a boundary string. It must be unique 
//so we use the MD5 algorithm to generate a random hash 
$random_hash = md5(date('r', time())); 
//define the headers we want passed. Note that they are separated with \r\n
$correo=$_GET['correo'];
$headers = "From: no-reply@animeflvapp.xyz\r\nReply-To: ".$correo; 
//add boundary string and mime type specification 
$headers .= "\r\nContent-Type: multipart/mixed; boundary=\"PHP-mixed-".$random_hash."\""; 
//read the atachment file contents into a string,
//encode it with MIME base64,
//and split it into smaller chunks
$attachment = file_get_contents($target_path2); 
    
//$mail_sent = @mail( $to, $subject, $attachment, $headers ); 
$mail_sent = true;
//if the message is sent successfully print "Mail sent". Otherwise print "Mail failed" 
echo $mail_sent ? "ok" : "error"; 

}else{
    echo "error";
}

?>