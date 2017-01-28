<?php
if (isset($_GET['set'])){
    $aid=$_GET['aid'];
    $file='../times/'.$aid.'.json';
    if (!file_exists($file)){
        $json = new stdClass();
        $json->aid=$aid;
        $json->daycode=date('N');
        $json->hour=date('~h:iA');
        file_put_contents($file,json_encode($json));
        header('Content-Type: application/json');
        if (isset($_GET['json'])){
            echo json_encode($json);
        }else{
            $resp = new stdClass();
            $resp->response='ok';
            echo json_encode($resp); 
        }
    }
}
if (isset($_GET['get'])){
    $file='../times/'.$_GET['aid'].'.json';
    if (file_exists($file)){
        header('Content-Type: application/json');
        echo file_get_contents($file);
    }
    else{
        header('Content-Type: application/json');
        $resp = new stdClass();
        $resp->response='error';
        echo json_encode($resp);
    }
}
if (isset($_GET['day'])){
    $file='../times/'.$_GET['aid'].'.json';
    if (file_exists($file)){
        $day=json_decode(file_get_contents($file));
        echo $day->daycode;
    }
    else{
        echo '0';
    }
}
if (isset($_GET['hour'])){
    $file='../times/'.$_GET['aid'].'.json';
    if (file_exists($file)){
        $hour=json_decode(file_get_contents($file));
        echo $hour->hour;
    }
    else{
        echo 'null';
    }
}
?>