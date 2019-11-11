function plotmotif(t,res,dim,index)

ind= index;
R=res(ind,1:4);
Q=res(ind,6);
L=res(ind,5);
d=res(ind,7);
dx=dim(ind);
[~, b]=sortrows(Q);
Rst=R(b,:);
m=max(d);

for i=1:size(Rst,1)
    disp(i)
    dimReal=dx(b(i));
    dimReal=dimReal{1};
    dims=dimReal+1;
    Rs=Rst(i,:);
    
    disp(['length ' num2str(L(b(i)))]);
    disp(['Loc: ' num2str(i)]);
    disp(Rs(1:4));
    
    for k=1:length(dims)
                subplot(length(dims),1,k);
                X=zscore(t(Rs(1,1):Rs(1,1)+L(b(i)),dims(k)));
                plot(X,'Color',[0,0.4470,0.7410],'LineWidth',2)
                hold on
                Y=zscore(t(Rs(1,3):Rs(1,3)+L(b(i)),dims(k)));
                %grey = [0.4,0.4,0.4];
                plot(Y,'Color','r','LineWidth',2)
                hold off
                title(['diminsion ' num2str(dims(k))]);
    end    
    pause
end