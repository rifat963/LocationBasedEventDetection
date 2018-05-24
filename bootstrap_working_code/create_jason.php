<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$data = array(
    'success'    =>    "Sweet",
    'failure'    =>    false,
    'array'      =>    array(),
    'numbers'    =>    array(1,2,3),
    'info'       =>    array(
                        'name'    =>    'Binny',
                        'site'    =>    'http://www.openjs.com/'
                 )
);

$data2 = array(
    'name'    =>    "Main",
   
    'children' =>    array(
                        'name'    =>    'Binny',
                        'site'    =>    'http://www.openjs.com/'
        
                 ),
                     array(
                        'name'    =>    'Binny',
                        'site'    =>    'http://www.openjs.com/'
                 )
);



//$json = json_encode($data2);

//echo $json;

$arr = array(
    array(
        "region" => "valore",
        "price" => "valore2"
    ),
    array(
        "region" => "valore",
        "price" => "valore2"
    ),
    array(
        "region" => "valore",
        "price" => "valore2"
    ),
     array(
        "region" => "valore",
        "price" => "valore2"
    )
);

$a1=array(
      array(
          "name"=>"test",
          "ch"=>$arr
      ),array(
          "name"=>"test",
          "ch"=>$arr
      )    
    
);

$data3=array('name'=>"Main",
            'children'=>$a1);

$fp = fopen('test.json', 'w');
//$data4=$arr_fruit=implode($data3,"");
//fwrite($fp, $data4);
fclose($fp);
file_put_contents('filename.json', json_encode($data3));
echo json_encode($data3);

//$encode = array("userdetails" => array(array("name" => "mohamedasif", "age" => "18")), "workdetails" => array(array("company_name" => "My Company", "role" => "web application developer", "employees" => "91")));


$d1=array("name"=>"er","children"=>"test");
$d2=array("name"=>"test");
$d3=array("name"=>"er","children"=>$d2);

$json_encode = json_encode($d3);
//echo $json_encode."<br/>";







//$data5=array();

