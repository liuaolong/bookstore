package com.laver.bookstore.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.laver.bookstore.service.IBookOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.laver.bookstore.domain.Book;
import com.laver.bookstore.domain.BookExample;
import com.laver.bookstore.domain.BookExample.Criteria;
import com.laver.bookstore.domain.Cart;
import com.laver.bookstore.domain.Comment;
import com.laver.bookstore.service.IBookService;
import com.laver.bookstore.service.ICartService;
import com.laver.bookstore.service.ICommentService;

@Controller
public class BookController {
	@Resource
	private IBookService bookService;

	@Resource
	private ICommentService commentService;

	@Resource
	private IBookOrderService bookOrderService;

	@RequestMapping("/index")
	public String index(Model model, HttpServletRequest request) {
		Set<String> bts = bookService.bookType();
		BookExample example = new BookExample();
		example.setOrderByClause("bid desc");
		List<Book> books = bookService.selectByExample(example);
//		List<Book> books = bookService.getAllBooks();
		List<Book> Cbooks = getCookies(request);
		model.addAttribute("books", books);
		model.addAttribute("Cbooks", Cbooks);
		model.addAttribute("bts", bts);
		return "front/index";
	}

	@RequestMapping("/addBook")
	public ModelAndView addUser(@RequestParam("image") MultipartFile image, HttpServletRequest request, String bname,
			String detail, String pirce, String type, String writer, String printer, String dateString, String images,
			Integer store) throws IllegalStateException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String img = uploadImg(image, request);

		Book book = new Book();
		book.setBname(bname);
		book.setDetail(detail);
		book.setPirce(pirce);
		book.setType(type);
		book.setWriter(writer);
		book.setPrinter(printer);
		book.setDate(date);
		book.setImage(img);
		book.setStore(store);
		bookService.addBook(book);

		return new ModelAndView("redirect:/manaBook.do");
	}

	@RequestMapping("/manaBook")
	public String manaBook(Integer pageNum, Model model) {
		if (pageNum != null) {
			PageHelper.startPage(pageNum, com.laver.bookstore.util.Constant.MB_PAGE_SIZE);
		} else {
			PageHelper.startPage(1, com.laver.bookstore.util.Constant.MB_PAGE_SIZE);
		}
		List<Book> books = bookService.findAllBook();
		PageInfo<Book> pageInfo = new PageInfo<Book>(books);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("books", books);
		return "manage/product";
	}

	@RequestMapping("/delBook")
	public ModelAndView deleteUser(Integer bid) {
		bookService.delById(bid);
		return new ModelAndView("redirect:/manaBook.do");
	}

	@RequestMapping("/modifyBookPage")
	public String modifyUserPage(Model model, Integer bid) {
		Book book = bookService.findById(bid);
		Set<String> bts = bookService.bookType();
		model.addAttribute("bts", bts);
		model.addAttribute("book", book);
		return "manage/product-modify";
	}

	@RequestMapping("/addproductPage")
	public String addproductPage(Model model) {
		Set<String> bts = bookService.bookType();
		model.addAttribute("bts", bts);
		return "manage/product-add";
	}

	@RequestMapping("/modifyBook")
	public ModelAndView modifyBook(@RequestParam("image") MultipartFile image, HttpServletRequest request,  String bname,
			String bid,String detail, String pirce, String type, String writer, String printer, String dateString, String images,
			Integer store)
			throws IllegalStateException, IOException {

		
		String img = uploadImg(image, request);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

		Book book = new Book();
		book.setBid(Integer.parseInt(bid));
		book.setBname(bname);
		book.setDetail(detail);
		book.setPirce(pirce);
		book.setType(type);
		book.setWriter(writer);
		book.setPrinter(printer);
		book.setDate(date);
		book.setImage(img);
		book.setStore(store);

		bookService.modifyBook(book);
		return new ModelAndView("redirect:/manaBook.do");
	}

	@RequestMapping("/bookView")
	public String bookView(Integer pageNum, Integer bid, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		setCookies(bid, request, response);
		Book book = bookService.findById(bid);
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(book.getDate());
		Set<String> bts = bookService.bookType();
		List<Book> Cbooks = getCookies(request);
		if (pageNum != null) {
			PageHelper.startPage(pageNum, com.laver.bookstore.util.Constant.C_PAGE_SIZE);
		} else {
			PageHelper.startPage(1, com.laver.bookstore.util.Constant.C_PAGE_SIZE);
		}
		List<Comment> comments = commentService.selectBybid(bid);
		PageInfo<Comment> pageInfo = new PageInfo<Comment>(comments);
		model.addAttribute("Cbooks", Cbooks);
		model.addAttribute("bts", bts);
		model.addAttribute("book", book);
		model.addAttribute("dateStr", dateStr);
		model.addAttribute("comments", comments);
		model.addAttribute("pageInfo", pageInfo);

		// --- TODO: djzhao --- //
		int orderNumber = bookOrderService.getOrderNumberByBid(bid);
		model.addAttribute("soldNum", orderNumber);

		return "front/product-view";
	}

	@RequestMapping("/productList")
	public String productList(Integer pageNum, Model model, String type, String key, HttpServletRequest request,
			HttpSession session) {
		Set<String> bts = bookService.bookType();
		BookExample example = (BookExample) session.getAttribute("example");
		if (example == null) {
			example = new BookExample();
		}
		if (type != null) {
			example.clear();
			Criteria cri = example.createCriteria();
			cri.andTypeEqualTo(type);
		}
		if (key != null) {
			example.clear();
			Criteria cri = example.createCriteria();
			cri.andBnameLike("%" + key + "%");
		}
		session.setAttribute("example", example);
		if (pageNum != null) {
			PageHelper.startPage(pageNum, com.laver.bookstore.util.Constant.PW_PAGE_SIZE);
		} else {
			PageHelper.startPage(1, com.laver.bookstore.util.Constant.PW_PAGE_SIZE);
		}
		List<Book> books = bookService.selectByExample(example);
		PageInfo<Book> pageInfo = new PageInfo<Book>(books);
		List<Book> Cbooks = getCookies(request);
		model.addAttribute("Cbooks", Cbooks);
		model.addAttribute("bts", bts);
		model.addAttribute("books", books);
		model.addAttribute("pageInfo", pageInfo);
		return "front/product-list";
	}

	List<Book> getCookies(HttpServletRequest request) {
		List<Book> Cbooks = new ArrayList<Book>();
		String list = "";
		// 从客户端获得Cookies集合
		Cookie[] cookies = request.getCookies();
		// 遍历这个Cookies集合
		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) {
				if (c.getName().equals("ListViewCookie")) {
					list = c.getValue();
				}
			}
		}
		if (list != "") {
			String[] arr = list.split("-");
			for (String s : arr) {
				Book book = bookService.findById(Integer.parseInt(s));
				Cbooks.add(book);
			}
		}
		return Cbooks;
	}

	void setCookies(Integer bid, HttpServletRequest request, HttpServletResponse response) {
		boolean flag = true;
		String list = "";
		// 从客户端获得Cookies集合
		Cookie[] cookies = request.getCookies();
		// 遍历这个Cookies集合
		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) {
				if (c.getName().equals("ListViewCookie")) {
					list = c.getValue();
				}
			}
		}
		// 如果浏览记录超过1000条，清零.
		String[] arr = list.split("-");
		if (list != "") {
			for (String s : arr) {
				if (Integer.parseInt(s) == bid) {
					flag = false;
				}
			}
		}
		if (flag) {
			list += bid + "-";
		}
		if (arr != null && arr.length > 0) {
			if (arr.length >= 10) {
				list = "";
			}
		}
		Cookie cookie = new Cookie("ListViewCookie", list);
		response.addCookie(cookie);
	}

	// --- TODO: djzhao --- //
	@RequestMapping("/addBookTypePage")
	public String addBookTypePage(Model model) {
		Set<String> bts = bookService.bookType();
		model.addAttribute("bts", bts);
		return "manage/type-add";
	}

	// 数据库设计不规范，该功能不予实现
	@RequestMapping("/addBookType")
	public String addBookType(Model model, String typename) {
		bookService.addBookType(typename);
		Set<String> bts = bookService.bookType();
		model.addAttribute("bts", bts);
		return "manage/type";
	}

	public String uploadImg(MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException {

		String path = null;// 文件路径
		String type = null;// 文件类型

		// 项目在容器中实际发布运行的根路径
		String realPath = request.getSession().getServletContext().getRealPath("/");

		Map<String, Object> map = new HashMap<String, Object>();

		// 尝试删除原先图片
		// String preImg = teacher.getPersonImg();
		// 设置存放图片文件的路径
		// path = realPath + "static/teacherimg/personphoto/" + preImg;
		// new File(path).delete();

		if (file != null) {// 判断上传的文件是否为空

			long size = file.getSize();

			if (size > 10000000) {

				return "文件超出大小！";
			}

			String fileName = file.getOriginalFilename();// 文件原名称
			// 判断文件类型
			type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
					: null;

			if (type != null) {// 判断文件类型是否为空
				if ("GIF".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase())
						|| "JPG".equals(type.toUpperCase())) {

					// System.out.println("项目在容器中实际发布运行的根路径" + realPath);

					// 自定义的文件名称
					String trueFileName = String.valueOf(System.currentTimeMillis()) + fileName;

					if (trueFileName.length() > 150) {// 文件名称太长

						trueFileName = trueFileName.substring(0, 13)
								+ trueFileName.substring(trueFileName.length() - 10, trueFileName.length());
					}

					/* System.getProperty("file.separator")+ */
					// 设置存放图片文件的路径
					path = realPath + "images/product/" + trueFileName;
					// 转存文件到指定的路径
					file.transferTo(new File(path));
					return trueFileName;
				} else {
					return "文件类型非图片格式";
				}
			} else {
				return "文件类型为空";
			}
		} else {

			return "没有找到相对应的文件";
		}

	}

}
