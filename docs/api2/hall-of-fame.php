<?php
header('HTTP/1.1 200 OK');
if (isset($_GET['list'])){
    header('Content-Type: application/json');
    echo file_get_contents("../fame/base.json");
}else if (isset($_GET['id'])){
    if (isset($_GET['image'])){
        header('Content-type: image/png');
        $file="../fame/imgs/".$_GET['id']."_".$_GET['image'].".png";
        if (file_exists($file)){
            echo file_get_contents($file);
        }else{
            header("HTTP/1.1 404 Not Found");
            $response=new stdClass();
            $response->response="failed";
            echo json_encode($response,JSON_PRETTY_PRINT);
        }
    }else if (isset($_GET['profile'])){
        header('Content-Type: application/json');
        $file="../fame/profiles/".$_GET['id'].".json";
        if (file_exists($file)){
            echo file_get_contents($file);
        }else{
            header("HTTP/1.1 404 Not Found");
            $response=new stdClass();
            $response->response="failed";
            echo json_encode($response,JSON_PRETTY_PRINT);
        }
    }else{
        header('Content-Type: application/json');
        header("HTTP/1.1 404 Not Found");
        $response=new stdClass();
        $response->response="failed";
        echo json_encode($response,JSON_PRETTY_PRINT);
    }
}else {
    header('Content-Type: application/json');
    header("HTTP/1.1 404 Not Found");
    $response=new stdClass();
    $response->response="failed";
    echo json_encode($response,JSON_PRETTY_PRINT);
}