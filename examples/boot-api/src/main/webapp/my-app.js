var myApp = new Framework7({
    animateNavBackIcon: true,
    // Enable templates auto precompilation
    precompileTemplates: true,
    // Enabled rendering pages using Template7
    template7Pages: true,
    // Specify Template7 data for pages
    template7Data: {
        about: {
            firstname: 'William ',
            lastname: 'Root',
            age: 27,
            position: 'Developer',
            company: 'TechShell',
        }
    }
});

var $$ = Dom7;

// Add main View
var mainView = myApp.addView('.view-main', {
    // Enable dynamic Navbar
    dynamicNavbar: true
});

$$('.login-screen .list-button').on('click', function () {
    var uname = $$('.login-screen input[name="username"]').val();
    var pwd = $$('.login-screen input[name="password"]').val();
    //david.wang
    var postdata = {};
    postdata.email = uname;
    postdata.password = pwd;

    var query = 'http://127.0.0.1:8080/api/accounts/login';
    $$.ajax({
        url: query,
        type: "get",
        contentType: "application/x-www-form-urlencoded",
        data: postdata,
        // if successful response received (http 2xx)
        success: function(data, textStatus ){
            console.log("close popup-login data="+data+";textStatus="+textStatus);
            // We have received response and can hide activity indicator
            data = JSON.parse(data);
            console.log("close popup-login data="+data);
            // myApp.alert('token='+data.token, function () {
            // });
            myApp.closeModal('.login-screen');
            localStorage.setItem('token', data.token);
            loadBooks();
            mybook();
            myborrowedbook();
        },
        error: function(xhr, textStatus, errorThrown){
        }
    });
});

$$('a.item-link.item-content.books').on('click',function () {
    console.log('所有图书 loadBooks click');
    loadBooks()
});

$$('a.item-link.item-content.mybook').on('click',function () {
    console.log('mybook click');
    mybook()
});

$$('a.item-link.item-content.request').on('click',function () {
    console.log('我借阅的图书');
    myborrowedbook()
});

$(document).ready(function () {
    //loadBooks();
})

$$(document).on('pageAfterAnimation', function (e) {
    $$('a.item-link.item-content.books').on('click',function () {
        console.log('所有图书 loadBooks click');
        loadBooks()
    });

    $$('a.item-link.item-content.mybook').on('click',function () {
        console.log('我拥有的图书 mybook click');
        mybook()
    });

    $$('a.item-link.item-content.request').on('click',function () {
        console.log('我借阅的图书');
        myborrowedbook()
    });
})

function loadBooks() {
    var query = 'http://127.0.0.1:8080/api/books?page=0&size=10';
    $$.ajax({
        url: query,
        type: "get",
        asyn: true,
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            data = JSON.parse(data);
            // console.log("books data="+data+"data.length="+data.length);
            console.log("books data="+data+data.length);
            var str ={
                'likes':[]
            }
            for(var i=0;i<data.length;i++) {
                var book = data[i];
                str.likes.push({'title':book.title,'description':book.status,'id':book.id});
            }
            console.log("str="+JSON.stringify(str));
            $('a.item-link.item-content.books').attr('data-context', JSON.stringify(str));
            $('p a.button.button-fill.borrow').on('click',function () {
                console.log("借阅 requestBook bookId=");
                requestBook(3);
            });
        }
    })
}

function mybook() {
    var query = 'http://localhost:8080/api/mybook?token='+localStorage.getItem('token');
    $$.ajax({
        url: query,
        type: "get",
        asyn: true,
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            data = JSON.parse(data);
            console.log("books data="+data+data.length);
            var str ={
                'likes':[]
            }
            var bookId;
            for(var i=0;i<data.length;i++) {
                var book = data[i];
                str.likes.push({'title':book.title,'description':book.status,'id':book.id});
                bookId = book.id;
            }
            console.log("我拥有的图书 str="+JSON.stringify(str));
            $('a.item-link.item-content.mybook').attr('data-context', JSON.stringify(str));
            $('a.button.button-fill.confirm').on('click',function () {
                console.log("同意借阅 confirm bookId="+bookId);
                confirmBook(3);
            });
            $('a.button.button-fill.markBookReturned').on('click',function () {
                console.log("确定归还 bookId=");
                markBookReturned(3);
            });
        }
    })
}

function myborrowedbook() {
    var query = 'http://localhost:8080/api/myborrowedbook?token='+localStorage.getItem('token');
    $$.ajax({
        url: query,
        type: "get",
        asyn: true,
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            data = JSON.parse(data);
            // console.log("books data="+data+"data.length="+data.length);
            console.log("books data="+data+data.length);
            var str ={
                'likes':[]
            }
            for(var i=0;i<data.length;i++) {
                var book = data[i];
                str.likes.push({'title':book.title,'description':book.status,'id':book.id});
            }
            console.log("我借阅的图书 str="+JSON.stringify(str));
            $('a.item-link.item-content.request').attr('data-context', JSON.stringify(str));
        }
    })
}

//确认借书
function confirmBook(id) {
    var query = 'http://localhost:8080/api/books/'+id+'/confirm?token='+localStorage.getItem('token');
    $$.ajax({
        url: query,
        type: "get",
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            console.log("确认借阅完成 books data="+data);
        }
    })
}

//借书
function requestBook(id) {
    var query = 'http://localhost:8080/api/books/'+id+'/request?token='+localStorage.getItem('token');
    $$.ajax({
        url: query,
        type: "get",
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            data = JSON.parse(data);
            console.log("确认借阅完成 books data="+data+data.length);
        }
    })
}

//确认归还
function markBookReturned(id) {
    var query = 'http://localhost:8080/api/books/'+id+'/return?token='+localStorage.getItem('token');
    $$.ajax({
        url: query,
        type: "get",
        contentType: "application/x-www-form-urlencoded",
        success: function(data, textStatus ){
            console.log("确认归还 books data="+data);
        }
    })
}