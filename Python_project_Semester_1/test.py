# t=int(input())
# for i in range(2,t):
#     if t%i==0:
#         print("given number is not prime")
#         break
# else:
#     print("given number is prime")    

# def sum(n):
#     x=int(input())
#     if n==1:
#         return x
#     return x+ sum(n-1)
# n=int(input("how many numbers you want to do sum"))
# print(sum(n))

# for i in range(0,58):
#     print(i)

# a=int(input())
# b=int(input())
# x=a,b
# b,a=x
# print(a)
# print(b)

# x = ['12', 'hello', 456]
# x[0] *= 3
# x[1][1] = 'bye'

# def computeresult():
#     try:
#         list1=[1,2,34,45,67,78,98]
#         sum1=sum(list1)
#         return sum1/len(list1)
#     except Exception :
#         return 0
# x=computeresult()
# print(x)    

# class Length_of_an_arc:
#     def __init__(self,radius,angle):
#         self.radius=radius
#         self.angle=angle
#     def arc_length(self):
#         self.arclength=2*3.14*self.radius*self.angle/360
#         print(self.arclength)
# radius=5
# angle=30        
# remote=Length_of_an_arc(radius,angle)        
# remote.arc_length()

# class bank:
#     def __init__(self,name,balance):
#         self.name=name
#         self.balance=balance
#     def deposit_money(self,amount):
#         self.balance+=amount
#         print("after deposting money your balance will be",self.balance)
#     def withdraw_money(self,amount):
#         if self.balance<amount:
#             print("Your account does not have sufficient balance")
#         else:    
#             self.balance-=amount
#             print("after withdrawing money your balance will be",self.balance)
#     def show_balance(self):
#         print("your accounts balance is",self.balance)
# name=input("enter the name of account holder")
# balance=0
# remote=bank(name,balance)
# print("welcome to the mittal bank")
# print("Please select any one option")    
# print("1. deposite money")
# print("2. withdraw money")
# print("3. show balance")
# ask=int(input("choice: "))      
# if ask==1:
#     amount=int(input("how many money you want to deposite "))
#     remote.deposit_money(amount)
# elif ask==2:
#     amount=int(input("how many money you want to withdraw "))
#     remote.withdraw_money(amount)       
# elif ask==3:
#     remote.show_balance()
# from functools import reduce
# cube= lambda x: x*x*x*x
# test= lambda x: x>4
# l=[1,2,3,4,5,6]
# list2=list(map(cube,l))
# print(list2)
# list3=list(filter(test,l))
# print(list3)
# rough= lambda x,y: x+y
# list4=reduce(rough,l)
# print(list4)

# import tkinter as tk
# win  = tk.Tk()

# win.geometry("1000x1000")
# win.config(background='white')
# win.title('sudarshan sucks')

# label=tk.Label(
#     win,
#     text="hi i am sudarshan khan",
#     font=("Arial",40,"bold italic"),
#     fg="blue",
#     bg="green",
#     relief="groove",
#     bd=40,
#     padx=15,
#     pady=20 
# )

# label.place(x=175,y=100)

# label2=tk.Label(
#     win,
#     text="i am member of isis and i have come here to destory india",
#     font=("Arial",40,"bold italic"),
#     fg="blue",
#     bg="green",
#     relief="groove",
#     bd=40,
#     padx=15,
#     pady=20)

# label2.place(x=100,y=400)

# win.mainloop()

# import turtle
# t=turtle.Turtle()
# t.forward(150)
# t.left(160)
# t.penup()
# t.forward(50)
# t.pendown()
# t.forward(100)
# turtle.done()

# import tkinter as tk
# t=tk.Tk()
# t.geometry('1000x1000')
# t.title("GUI yash")
# t.config(background="green")
# label=tk.Label(
#     t,
#     text="hello my name is yash",
#     font=("Arial",30,"bold italic"),
#     fg="blue",
#     bg="red",
#     relief="groove",
#     bd=40,
#     padx=20,
#     pady=20
# )
# label.place(x=10,y=10)
# i=0
# def submit():
#     global i
#     i+=1
#     word=entry.get()
#     print(word)
#     label.config(text=str(i))

# def delete():
#     entry.delete(0,"end")   
# def backspace():
#     entry.delete(len(entry.get())-1,"end")     

# entry=tk.Entry(t,
#                font=("Arial",30)
#                )
# entry.pack(padx=10,pady=200,side="left")

# submit_button=tk.Button(t,text="submit",command=submit,font=("Arial",30))
# submit_button.pack(side="right")
# delete_button=tk.Button(t,text="delete",command=delete,font=("Arial",30))
# delete_button.pack(side="right")
# backspace_button=tk.Button(t,text="backspace",command=backspace,font=("Arial",30))
# backspace_button.pack(side="right")

# def display():
#     if x.get()==1:
#         label2.config(text="you agreed")
#     else:
#         label2.config(text="you disagreed")    

# x=tk.IntVar()
# check_button=tk.Checkbutton(
#     t,
#     text="terms and condition",
#     variable=x,
#     offvalue=0,
#     onvalue=1,
#     command=display
# )
# check_button.pack(pady=20)
# label2=tk.Label(t,text="")
# label2.pack(pady=20)

# import tkinter as tk

# def show_selection():
#     selected_option = var.get()
#     label.config(text=f"Selected option: {selected_option}")

# root = tk.Tk()
# root.title("Checkbutton and Radiobutton Example")

# var = tk.StringVar()

# # Checkbutton
# check_button = tk.Checkbutton(root, text="Check me", variable=var, onvalue="Checked", offvalue="Unchecked")
# check_button.pack(pady=10)

# # Radiobuttons
# radio_button1 = tk.Radiobutton(root, text="Option 1", variable=var, value="Option 1")
# radio_button2 = tk.Radiobutton(root, text="Option 2", variable=var, value="Option 2")

# radio_button1.pack(pady=5)  # Use pack() for the first radiobutton
# radio_button2.pack(pady=5)  # Use pack() for the second radiobutton

# button = tk.Button(root, text="Show Selection", command=show_selection)
# button.pack(pady=10)

# # Label to display selected option
# label = tk.Label(root, text="Selected option:")
# label.pack(pady=10)

# # Run the main application loop
# root.mainloop()

# import tkinter as tk

# def show_selected_item():
#     selected_item = listbox.get(tk.ACTIVE)
#     label.config(text=f"Selected item: {selected_item}")

# root = tk.Tk()
# root.title("Listbox Example")

# listbox = tk.Listbox(root)
# listbox.pack(pady=10)

# for item in ["Item 1", "Item 2", "Item 3", "Item 4"]:
#     listbox.insert(tk.END, item)

# button = tk.Button(root, text="Show Selected Item", command=show_selected_item)
# button.pack(pady=10)
# label = tk.Label(root, text="")
# label.pack(pady=10)

# root.mainloop()
# radio_button1.pack()
# radio_button2.pack()

# button = tk.Button(root, text="Show Selection", command=show_selection)
# button.pack(pady=10)

# label = tk.Label(root, text="")
# label.pack(pady=10)

# root.mainloop()

# class Bank:
#     def __init__(self,name,id,account_number,money):
#         self.name=name
#         self.id=id
#         self.account_number=account_number
#         self.money=money
#     def set_email(self,email):
#         self.email=email
# name=input()
# account_number=int(input())
# id=int(input())
# money=int(input())
# email=input()
# obj=Bank(name,id,account_number,money)
# obj.set_email(email)

# p=int(input())
# q=int(input())
# m=[]
# for i in range(1,p+1):
#     print("enter the details of student",i)
#     t=[]
#     for j in range(1,q+1):
#         print("enter the marks of subject",j)
#         x=int(input())
#         t.append(x)
#     m.append(t)
# for i in range(p):
#     max=m[i][0]
#     for j in range(q):
#         if m[i][j]>max:
#             max=m[i][j]
#     print("max marks scored by student",i,"is",max)        

# dict={"red":1,"green":True,4:"yellow"}
# fileout=open("test.txt","w")
# fileout.write(str(dict))
# fileout.close()

# fileout=open("text.txt","r")
# ask=int(input())
# for j,i in enumerate(fileout):
#     if j+1==ask:
#         print(i)
#         break
# fileout.close()   

# my_dict = {'x':500, 'y':5874, 'z': 560}
# kmax = max(my_dict.keys(), key=(lambda k: my_dict[k]))
# kmin = min(my_dict.keys(), key=(lambda k: my_dict[k]))
# print(kmax)
# print('MValue: ',my_dict[kmax])
# print('MValue: ',my_dict[kmin])

# k={}
# d1={"a":100,"c":200,"e":300}
# d2={"a":200,"b":300,"d":500}
# x=set(d1.keys()).union(set(d2.keys()))
# for i in x:
#     if i in d1 and i in d2:
#         k[i]=d1[i]+d2[i]
#     elif i in d1 :
#         k[i]=d1[i]
#     elif i in d2 :
#         k[i]=d2[i]
# print(k)
   
# y=input()
# y=y[::-1]
# for i in y:
#     print(int(i))

# y=input()
# if y==y[::-1]:
#     print("given number is a palindrone number")
# else:
#     print("given number is not a palindrone number")  

# string="You are not end a sentence with because because is a conjunction"
# x={}
# for i in string.split():
#     if i not in x:
#         x[i]=1
#     else:
#         x[i]+=1
# for i in x:
#     print(f"'{i}' appreared {x[i]} times") 

# fileout=open("text.txt","r")
# try:
#     x=fileout.read()
#     list1=x.split(".")
#     print(len(list1)-1) 
# except:
#     print("File reading error!!!")   

# n=int(input())
# m=int(input())
# d1=int(input())
# d2=int(input())
# sum=0
# for i in range(n,m+1):
#     if i%d1==0 and i%d2!=0:
#         sum+=i 
# print(sum) 

# def myfun(L):
#     L[0]= [5, 6, 7]
#     L[:1] = [20]
#     print(L)
#     return L
# X = [[1, 1, 1], [2, 2, 2], [3, 3, 3]]
# d=myfun(X[:1])
# print(X)

# class A:
#     def __init__(self):
#         self.x = 2

# class B(A):
#     def __init__(self):
#         super().__init__()
#         self.y = 3

# b = B()
# print(b.y)
# print(b.x)

# import tkinter as tk
# obj=tk.Tk()
# obj.geometry("420x420")
# obj.title("listbox")
# def print1():
#     canvas.create_rectangle(50,50,150,150,fill="blue")
# canvas=tk.Canvas(obj,width=200,height=200)
# canvas.pack(pady=10)
# label=tk.Label(obj,text="hello world")
# label.pack(pady=10)
# button=tk.Button(obj,text="press",command=print1)
# button.pack(pady=10)


# obj.mainloop()

# import tkinter as tk
# root = tk.Tk()
# root.title("Grid Layout Example")
# # Creating labels
# label1 = tk.Label(root, text="Label 1")
# label2 = tk.Label(root, text="Label 2")
# label3 = tk.Label(root, text="Label 3")
# # Placing labels using the grid layout manager
# label1.grid(row=0, column=0)
# label2.grid(row=0, column=1)
# label3.grid(row=1, column=0, columnspan=2)
# root.mainloop()


n=int(input())
a=[]
for i in range(n):
    x=[]
    for j in input().split():
        x.append(j) 
    a.append(x)    
sum1=0    
for i in range(n):
    sum1+=int(a[i][i])
sum2=0
j=0
for i in range(n-1,-1,-1):
     sum2+=int(a[j][i])
     j+=1
print(abs(sum1-sum2))     