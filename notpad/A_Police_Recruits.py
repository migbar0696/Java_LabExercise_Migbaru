import sys
input = sys.stdin.readline

n = int(input())

listn = list(map(int, input().split()))

sumn = 0
cnt = 0

for num in listn:
    if num == -1 and sumn == 0:
        # print("here")
        cnt += 1
    if num > 0 or (sumn > 0 and num == -1):
        sumn += num
        # print(sumn)
print(cnt)