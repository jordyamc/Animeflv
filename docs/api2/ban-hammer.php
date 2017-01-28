<?php
$dir="../bans/";
$get='get';
$set='set';
$pass='grim042a677';
$type=$_GET["type"];
$id=$_GET["id"];
$password=$_GET["password"];
$status_app=$_GET["status_app"];
$status_chat=$_GET["status_chat"];
if (isset($type)){
    if ($type==$get){
        if (isset($id)){
            $file=$dir.$id.".txt";
            if (file_exists($file)){
                $status=file_get_contents($file);
                echo $status;
            }
            else{
                file_put_contents($file,"ok:::ok");
                $status=file_get_contents($file);
                echo $status;
            }
        }
        else{
            echo "Falta Informacion";
        }
    }
    if ($type==$set){
        if (isset($id) and isset($password) and isset($status_app) and isset($status_chat)){
            if ($password==$pass){
                $file=$dir.$id.".txt";
                file_put_contents($file,$status_app.":::".$status_chat);
                echo "ID: ".$id."<br>";
                echo "New Status app: ".$status_app."<br>";
                echo "New Status chat: ".$status_chat."<br>";
            }else{
                echo "Error en contraseña";
            }
        }
        else{
        echo "Falta Informacion";
        }
    }
}
else{
    echo "<html><body>".
        "<center><h1>STATUS</h1></center>".
        '<p>Password:</p>'.
        '<input type="text" id="pass"/>'.
        '<p>ID:</p>'.
        '<input type="text" id="id"/>'.
        "<p>STATUS App:</p>".
        '<input type="radio" id="app1" value="ok"/>OK'.
        '<input type="radio" id="app2" value="ban"/>BAN'.
        "<p>STATUS Chat:</p>".
        '<input type="radio" id="chat1" value="ok"/>OK'.
        '<input type="radio" id="chat2" value="ban"/>BAN'.
        "<br>".
        '<input type="button" value="LIMPIAR" onclick="c();"/>'.
        "<br>".
        '<input type="button" value="ENVIAR" onclick="send();"/>'.
        "</body>".
        "<script>".
        "function send(){".
        'var id = document.getElementById("id").value;'.
        'var app1Check = document.getElementById("app1").checked;'.
        'var app2Check = document.getElementById("app2").checked;'.
        'var chat1Check = document.getElementById("chat1").checked;'.
        'var chat2Check = document.getElementById("chat2").checked;'.
        'var pass = document.getElementById("pass").value;'.
        'if (app1Check){var app=document.getElementById("app1").value;};'.
        'if (app2Check){var app=document.getElementById("app2").value;};'.
        'if (chat1Check){var chat=document.getElementById("chat1").value;};'.
        'if (chat2Check){var chat=document.getElementById("chat2").value;};'.
        'window.location.href = "http://necrotic-neganebulus.hol.es/ban-hammer.php?type=set&id="+id+"&password="+pass+"&status_app="+app+"&status_chat="+chat;};'.
        "function c(){".
        'document.getElementById("app1").checked=false;'.
        'document.getElementById("app2").checked=false;'.
        'document.getElementById("chat1").checked=false;'.
        'document.getElementById("chat2").checked=false;'.
        "};</script>".
        "</html>";
}
?>