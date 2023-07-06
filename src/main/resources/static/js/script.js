console.log("This is script js file")

const toggleSidebar = () =>{

    if($('.sidebar').is(":visible")){
        $('.sidebar').css("display","none");
        $('.content').css("margin-left","0%");
    }else{
        $('.sidebar').css("display","block");
        $('.content').css("margin-left","20%");
    }

}
const search=()=>{
    // console.log("searching");
    let query=$("#search-input").val();
    if(query==''){
        $(".search-result").hide();

    }else{
        // console.log(query);
        let url=`http://localhost:8001/search/${query}`;
        fetch(url).then(response=>{
          return response.json();
        }).then(data=>{
            // console.log(data);
            let text=`<div class='list-group'>`
            data.forEach((contact)=> {
                text+=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });
            text+=`</div>`
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
}

//First Request to server to create the order
const paymentStart=()=>{
    console.log("payment started");
    let amount=$("#payment_field").val();
    console.log(amount);
    if(amount=='' || amount==null){
        alert("amount is required");
        return;
    }
    //code
    //we will use ajax to send the request to server to create order
    $.ajax({
        url:'/user/create_order',
        data: {amount:amount},
        contentType: 'application/json',
        type:'POST',
        dataType:'json',
        success:function(response){
            //invoked when success
        },
        error:function(error){
            //invoked when error
            console.log(error);
            alert('something went wrong');
        }
    })
}
