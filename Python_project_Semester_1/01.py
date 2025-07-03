from flask import Flask, render_template, request, redirect, url_for, flash
import mysql.connector as sqltor
import random
from datetime import date, timedelta

app = Flask(__name__)
app.secret_key = 'library_management'  
db = sqltor.connect(user='root', password='Yash@2006', host='localhost', database='library')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        user_name = request.form['username']
        user_id = request.form['user_id']
        password = request.form['password']
        verification_code = request.form['verification_code']
        if verification_code != request.form['generated_code']:
            flash('Incorrect verification code!', 'error')
            return redirect(url_for('login'))
        cursor = db.cursor()
        query = "SELECT * FROM users WHERE User_Name = %s AND User_ID = %s AND Password = %s"
        val = (user_name, user_id, password)
        cursor.execute(query, val)
        result = cursor.fetchall()
        if len(result) == 0:
            flash('Invalid User Name, User ID, or Password!', 'error')
            return redirect(url_for('login'))
        else:
            flash('Access Granted!!!', 'success')
            if user_id[0]=="u" or user_id[0]=="U":
                return redirect(url_for('user_options'))
            else:
                return redirect(url_for('admin_options'))   
    generated_code = ''.join(str(random.randint(1, 9)) for _ in range(5))
    return render_template('login.html', generated_code=generated_code)

@app.route('/forgot_password', methods=['GET', 'POST'])
def forgot_password():
    if request.method == 'POST':
        user_name = request.form['username']
        user_id = request.form['userid']
        cursor = db.cursor()
        Q = "SELECT * FROM users WHERE user_name=%s AND user_id=%s"
        val = (user_name, user_id)
        cursor.execute(Q, val)
        result = cursor.fetchall()
        if result:
            return render_template('reset_password.html', username=user_name, userid=user_id)
        else:
            flash("Details are not correct. Sorry, you can't change the password.", 'error')
            return redirect(url_for('login'))
    return render_template('forgot_password.html')

@app.route('/reset_password', methods=['POST'])
def reset_password():
    user_name = request.form['username']
    user_id = request.form['userid']
    password = request.form['new_password']
    cpassword = request.form['confirm_password']
    if password == cpassword:
        cursor = db.cursor()
        Q = "UPDATE users SET password=%s WHERE user_name=%s AND user_id=%s"
        val = (password, user_name, user_id)
        cursor.execute(Q, val)
        db.commit()
        flash("Password changed successfully", 'success')
        return redirect(url_for('login'))
    else:
        flash("Both entered passwords are different. Please try again.", 'error')
        return redirect(url_for('forgot_password'))

@app.route('/user_options')
def user_options():
    return render_template('user_options.html')

@app.route('/admin_options')
def admin_options():
    return render_template('admin_options.html')    

@app.route('/issue_book',methods=['GET','POST'])
def issue_book():
    if request.method == 'POST':
        book_id = request.form['book_id']
        user_id = request.form['user_id']
        cursor = db.cursor()
        Q = "SELECT * FROM book WHERE book_id = %s"
        cursor.execute(Q, (book_id,))
        book = cursor.fetchone()
        if not book:
            flash("Invalid Book ID", "error")
            return redirect(url_for('issue_book'))
        Q_availability = "SELECT * FROM book WHERE book_id = %s AND book_availability = 'Yes'"
        Q_fine = "SELECT fine FROM users WHERE user_id = %s" 
        cursor.execute(Q_availability, (book_id,))
        available_book = cursor.fetchone()
        cursor.execute(Q_fine, (user_id,))
        user_fine = cursor.fetchone()
        if available_book and (int(user_fine[0]) == 0):
            doi = date.today()
            dor = doi + timedelta(days=7)
            Q_issue = "UPDATE book SET book_availability = 'No', issue_date = %s, return_date = %s, user_id = %s WHERE book_id = %s"
            cursor.execute(Q_issue, (doi, dor, user_id, book_id))
            db.commit()
            flash(f"Book Issued Successfully! Due Date: {dor}", "success")
            return redirect(url_for('issue_book'))
        elif user_fine and user_fine[0] > 0:
            flash("Pending fine. Please clear it before issuing a new book.", "error")
        else:
            flash("Sorry! The book is not available.", "error")
        return redirect(url_for('issue_book'))
    return render_template("issue_book.html")

@app.route('/return_book',methods=["GET",'POST'])
def return_book():
    if request.method == 'POST':
        book_id = request.form['book_id']
        cursor = db.cursor()
        query = "SELECT * FROM book WHERE book_id = %s"
        cursor.execute(query, (book_id,))
        result = cursor.fetchall()
        if not result:
            flash("Invalid Book ID", "error")
            return redirect(url_for('return_book'))
        query = "SELECT * FROM book WHERE book_id = %s AND book_availability = 'No'"
        cursor.execute(query, (book_id,))
        result = cursor.fetchall()
        if not result:
            flash("This book is already returned or not issued.", "error")
            return redirect(url_for('return_book'))
        odor = result[0][5]
        user_id = result[0][1]
        dor = date.today()
        penalty = max(0, (dor - odor).days * 10)
        query = "UPDATE book SET book_availability='Yes', issue_date=NULL, return_date=NULL, user_id=NULL WHERE book_id=%s"
        cursor.execute(query, (book_id,))
        query = "UPDATE users SET fine=%s WHERE user_id=%s"
        cursor.execute(query, (penalty, user_id))
        db.commit()
        flash(f"Book returned successfully! Penalty due: ₹{penalty}.", "success")
        return redirect(url_for('return_book'))
    return render_template('return_book.html')

@app.route('/fine_finder', methods=['GET', 'POST'])
def fine_finder():
    if request.method == 'POST':
        user_id = request.form['user_id']
        cursor = db.cursor()
        query = "SELECT fine FROM users WHERE user_id = %s"
        cursor.execute(query, (user_id,))
        result = cursor.fetchall()
        if len(result) == 0:
            flash('Invalid User ID!', 'error')
            return redirect(url_for('fine_finder'))
        else:
            fine_amount = result[0][0]
            return render_template('pay_fine.html', fine_amount=fine_amount, user_id=user_id)
    return render_template('fine_finder.html')

@app.route('/pay_fine', methods=['POST'])
def pay_fine():
    user_id = request.form['user_id']
    amount_to_pay = int(request.form['amount'])
    cursor = db.cursor()
    query = "SELECT fine FROM users WHERE user_id = %s"
    cursor.execute(query, (user_id,))
    result = cursor.fetchall()
    if len(result) == 0:
        flash('Invalid User ID!', 'error')
        return redirect(url_for('fine_finder'))
    else:
        current_fine = result[0][0]
        if current_fine == 0:
            flash('You have no fine to pay!', 'info')
            return redirect(url_for('fine_finder'))
        remaining_fine = current_fine - amount_to_pay
        if remaining_fine < 0:
            flash('Payment amount exceeds the fine amount!', 'error')
            return redirect(url_for('fine_finder'))
        query = "UPDATE users SET fine = %s WHERE user_id = %s"
        cursor.execute(query, (remaining_fine, user_id))
        db.commit()
        flash(f"Remaining fine: {remaining_fine}", 'success')
        return redirect(url_for('fine_finder'))

@app.route('/show_books')
def show_books():
    cursor = db.cursor()
    cursor.execute("SELECT book_id, book_name, book_author, book_availability FROM book")
    result = cursor.fetchall()
    cursor.close()
    if not result:
        books = []
    else:
        books = [{'book_id': row[0], 'book_name': row[1], 'book_author': row[2], 'book_availability': row[3]} for row in result]
    return render_template('show_books.html', books=books)

@app.route('/logout')
def logout():
    return redirect(url_for('login'))

@app.route('/add_user_admin', methods=['GET', 'POST'])
def add_user_admin():
    if request.method == 'POST':
        choice = request.form.get('choice')
        user_name = request.form.get('user_name')
        user_id_prefix = 'U' if choice == '1' else 'A'
        user_id = user_id_prefix + request.form.get('user_id')
        password = request.form.get('password')
        cursor = db.cursor()
        if choice == '1':
            query = "INSERT INTO users(user_name, user_id, password) VALUES (%s, %s, %s)"
            cursor.execute(query, (user_name, user_id, password))
            db.commit()
            flash('User Details Added Successfully!', 'success')
        elif choice == '2':
            query = "INSERT INTO users(user_name, user_id, password) VALUES (%s, %s, %s)"
            cursor.execute(query, (user_name, user_id, password))
            db.commit()
            flash('Admin Details Added Successfully!', 'success')
        else:
            flash('Invalid Choice! Please try again.', 'error')
        return redirect(url_for('add_user_admin'))
    return render_template('add_user_admin.html')

@app.route('/add_book', methods=['GET', 'POST'])
def add_book():
    if request.method == 'POST':
        book_id = request.form.get('book_id')
        book_name = request.form.get('book_name')
        book_author = request.form.get('book_author')
        cursor = db.cursor()
        insert_query = "INSERT INTO book (book_id, book_name, book_author) VALUES (%s, %s, %s)"
        cursor.execute(insert_query, (book_id, book_name, book_author))
        db.commit()
        flash('Book Details Added Successfully', 'success')
        cursor.close()
        return redirect(url_for('add_book'))
    return render_template('add_book.html')

@app.route('/delete_user', methods=['GET', 'POST'])
def delete_user():
    if request.method == 'POST':
        user_id = request.form.get('user_id')
        cursor = db.cursor()
        check_query = "SELECT * FROM users WHERE user_id = %s"
        cursor.execute(check_query, (user_id,))
        result = cursor.fetchall()
        if len(result) == 0:
            flash('Invalid User ID. Please check and try again.', 'error')
        else:
            delete_query = "DELETE FROM users WHERE user_id = %s"
            cursor.execute(delete_query, (user_id,))
            db.commit()
            flash('User Details Deleted Successfully', 'success')
        cursor.close()
        return redirect(url_for('delete_user'))
    return render_template('delete_user.html')

def login1(user_id, password):
    Q = "SELECT * FROM users WHERE User_ID = %s AND Password = %s"
    val = (user_id, password)
    cursor = db.cursor()
    cursor.execute(Q, val)
    result = cursor.fetchall()
    if len(result) == 0:
        return False
    else:
        if result[0][1][1] == 'H' or result[0][1][1]=="h": 
            return True
        return False

@app.route('/del_admin', methods=['GET', 'POST'])
def del_admin():
    if request.method == 'POST':
        user_id = request.form['user_id']
        password = request.form['password']
        if login1(user_id, password):
            admin_id = request.form['admin_id']
            cursor = db.cursor()
            query_check = "SELECT * FROM users WHERE user_id = %s"
            cursor.execute(query_check, (admin_id,))
            result = cursor.fetchall()
            if len(result) == 0:
                flash("Invalid Admin ID.", "error")
            else:
                query_delete = "DELETE FROM users WHERE user_id = %s"
                cursor.execute(query_delete, (admin_id,))
                db.commit()
                flash("Admin details deleted successfully.", "success")
            cursor.close()
        else:
            flash("You are not authorized to delete admin details.", "error")
        return redirect(url_for('del_admin'))
    return render_template('del_admin.html')

@app.route('/delete_book', methods=['GET', 'POST'])
def delete_book():
    if request.method == 'POST':
        book_id = request.form['book_id']
        cursor = db.cursor()
        check_query = "SELECT * FROM book WHERE book_id = %s"
        cursor.execute(check_query, (book_id,))
        result = cursor.fetchall()
        if len(result) == 0:
            flash("Invalid Book ID. No such book found.", "error")
        else:
            delete_query = "DELETE FROM book WHERE book_id = %s"
            cursor.execute(delete_query, (book_id,))
            db.commit()
            flash("Book deleted successfully!", "success")
        return redirect(url_for('delete_book')) 
    return render_template('delete_book.html') 

@app.route('/show_admins')
def show_admins():
    cursor = db.cursor()
    cursor.execute("SELECT user_name, user_id FROM Users WHERE user_id LIKE 'A%'")
    result = cursor.fetchall()
    if len(result) == 0:
        return render_template('show_admins.html', message="No Record was Found")
    return render_template('show_admins.html', admins=result)

@app.route('/show_users')
def show_users():
    cursor = db.cursor()
    cursor.execute("SELECT user_name, user_id, fine FROM Users WHERE user_id LIKE 'U%'")
    result = cursor.fetchall()
    if len(result) == 0:
        return render_template('show_users.html', message="No Record was Found")
    return render_template('show_users.html', users=result)

@app.route('/show_booksA')
def show_booksA():
    cursor = db.cursor()
    cursor.execute("SELECT book_id,user_id,book_name, book_author, book_availability, return_date,issue_date FROM book")
    result = cursor.fetchall()
    cursor.close()
    if not result:
        books = []
    else:
        books = [{'book_id': row[0],'user_id': row[1] , 'book_name': row[2], 'book_author': row[3], 'book_availability': row[4],'return_date': row[5],'issue_date' : row[6]} for row in result]
    return render_template('show_booksA.html', books=books)

if __name__ == '__main__':
    app.run(debug=True,port=5005)