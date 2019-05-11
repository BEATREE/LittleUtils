package actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.opensymphony.xwork2.ActionSupport;

import beans.User;
import utils.HibernateUtil;
import utils.Page;
import utils.PageUtil;

/**
 * 项目名称：EnglishOnlineRegistrationSystem 类名称：AllContestant 类描述：
 * 
 * @author Administrator 创建时间：2018年11月22日 下午1:51:58
 */
public class AllContestant extends ActionSupport {
	private ArrayList<User> list;
	private String currentPageStr;

	public String getCurrentPageStr() {
		return currentPageStr;
	}

	public void setCurrentPageStr(String currentPageStr) {
		this.currentPageStr = currentPageStr;
	}

	public ArrayList<User> getList() {
		return list;
	}

	public void setList(ArrayList<User> list) {
		this.list = list;
	}

	@Override
	public String execute() throws Exception {
		int currentPage = 0;// 设置当前页
		if ((currentPageStr == null) || ("".equals(currentPageStr))) {
			currentPage = 1;
		} else {
			currentPage = Integer.parseInt(currentPageStr);
		}
		Page page = PageUtil.createPage(10, findAllCount(), currentPage);
		ArrayList<User> user = findAllUser(page);
		this.list = user;
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		request.setAttribute("list",list);
		session.setAttribute("ADpage", page);
		return SUCCESS;
	}

	// 分頁查詢所有用戶
	public ArrayList<User> findAllUser(Page page) {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from User");
		query.setFirstResult(page.getBeginIndex());
		query.setMaxResults(page.getEveryPage());
		ArrayList<User> user = (ArrayList<User>) query.list();
		tx.commit();
		return user;
	}

	// 查询所有记录数
	public int findAllCount() {
		String findSQL = "SELECT COUNT( * ) FROM usertable";
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery(findSQL);
		int count = ((Number) query.uniqueResult()).intValue();
		tx.commit();
		return count;

	}
}
