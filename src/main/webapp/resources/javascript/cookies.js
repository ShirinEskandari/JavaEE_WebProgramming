window.addEventListener('DOMContentLoaded', updateCartNum);
/**
 * Authors: Grant, Alex and Finley
 * 
 */

/**
 * Function used to set a cookie with the bookID in the browser when the user adds a book to the cart.
 * Updates the cart number in the UI header
 * @param {type} bookID
 * @returns {undefined}
 */
function setCookie(bookID){
    document.cookie = "bookID" + bookID + "=" + bookID;
    updateCartNum();
}

/**
 * Method that removes a cookie and removes an item from the cart.
 * @param {type} bookID
 * @returns {undefined}
 */
function removeBookFromCart(bookID){
    document.cookie = "bookID" + bookID + "=;" + "expires = Thu, 01 Jan 1970 00:00:00 GMT";
}

/**
 * Method that sets the last clicked genre by the user
 * @param {type} genre
 * @returns {undefined}
 */
function lastClickedGenre(genre){
    document.cookie = "genre" + "=" + genre;
}

/**
 * Method that deletes all cookies
 * @returns {undefined}
 */
function deleteAllCookies(){
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var val = cookie.indexOf("=");
        var name = val > -1 ? cookie.substr(0, val) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}
/**
 * Function used to update the text that displays amount of items in cart, only if cookies exist (meaning there is a cart)
 * @returns {undefined}
 */
function updateCartNum(){
    if(document.cookie){
        var cookies = document.cookie;
        var numTotalCookies = cookies.split(";");
        var num = 0;
        for(var i=0; i<numTotalCookies.length; i++){
            if(numTotalCookies[i].includes("bookID")){
                num++;
            }
        }
        var cartMsg = document.querySelector("#cartNumItems");
        if(cartMsg){
            cartMsg.innerText = num;
        }
    }
}


/**
 * Function used to confirm to the user that their review will be submitted
 * @param {type} bookID
 * @returns {undefined}
 */
function addedReview(bookID) {
    var reviewInput = document.getElementById("review" + bookID).value;
    if (reviewInput === "" || reviewInput === null) {
        alert("You must write something to submit a review!");
    } else {
        alert("Thank you for your review! Your review will be verified before being posted!");
    }
}